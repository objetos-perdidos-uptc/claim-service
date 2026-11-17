package com.objetosperdidos.claimservice.infrastructure.in.web;

import com.objetosperdidos.claimservice.application.dto.CreateClaimCommand;
import com.objetosperdidos.claimservice.application.dto.CreateClaimResult;
import com.objetosperdidos.claimservice.application.dto.ResolveClaimCommand;
import com.objetosperdidos.claimservice.application.dto.VerifyOtpCommand;
import com.objetosperdidos.claimservice.application.port.in.*;
import com.objetosperdidos.claimservice.domain.model.Claim;
import com.objetosperdidos.claimservice.infrastructure.in.web.dto.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/claims")
public class ClaimController {

    private final CreateClaimUseCase createUC;
    private final VerifyOtpUseCase verifyUC;
    private final ResendOtpUseCase resendUC;
    private final ResolveClaimUseCase resolveUC;
    private final GetClaimUseCase getUC;
    private final List<String> resolveRoles;

    public ClaimController(CreateClaimUseCase createUC, VerifyOtpUseCase verifyUC,
                           ResendOtpUseCase resendUC, ResolveClaimUseCase resolveUC,
                           GetClaimUseCase getUC,
                           @Value("${claims.resolve.allowed-roles:ADMIN}") List<String> resolveRoles) {
        this.createUC = createUC;
        this.verifyUC = verifyUC;
        this.resendUC = resendUC;
        this.resolveUC = resolveUC;
        this.getUC = getUC;
        this.resolveRoles = resolveRoles;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateClaimHttpResponse create(@RequestHeader("X-User-Id") String userId,
                                          @Valid @RequestBody CreateClaimHttpRequest req) {
        CreateClaimResult r = createUC.create(userId,
                new CreateClaimCommand(req.reportId(), req.description(), req.contactPhone()));
        return new CreateClaimHttpResponse(r.claim(), r.devOtp());
    }

    @PostMapping("/{id}/otp/verify")
    public Claim verify(@PathVariable String id,
                        @RequestHeader("X-User-Id") String userId,
                        @Valid @RequestBody OtpVerifyHttpRequest req) {
        return verifyUC.verify(new VerifyOtpCommand(id, userId, req.code()));
    }

    @PostMapping("/{id}/otp/resend")
    public Map<String, String> resend(@PathVariable String id,
                                      @RequestHeader("X-User-Id") String userId) {
        return Map.of("devOtp", resendUC.resend(id, userId));
    }

    @GetMapping("/me")
    public List<Claim> mine(@RequestHeader("X-User-Id") String userId) {
        return getUC.byClaimant(userId);
    }

    @GetMapping("/{id}")
    public Claim get(@PathVariable String id) { return getUC.byId(id); }

    @GetMapping("/report/{reportId}")
    public List<Claim> byReport(@PathVariable String reportId) {
        return getUC.byReport(reportId);
    }

    @PatchMapping("/{id}/resolve")
    public Claim resolve(@PathVariable String id,
                         @RequestHeader("X-User-Role") String role,
                         @RequestBody ResolveClaimHttpRequest req) {
        boolean allowed = resolveRoles.stream().anyMatch(r -> r.trim().equalsIgnoreCase(role));
        if (!allowed)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Tu rol no puede resolver reclamos");
        return resolveUC.resolve(new ResolveClaimCommand(id, req.status()));
    }
}