package com.objetosperdidos.claimservice.controller;

import com.objetosperdidos.claimservice.dto.CreateClaimRequest;
import com.objetosperdidos.claimservice.dto.OtpVerifyRequest;
import com.objetosperdidos.claimservice.dto.ResolveClaimRequest;
import com.objetosperdidos.claimservice.model.Claim;
import com.objetosperdidos.claimservice.service.ClaimService;
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

    private final ClaimService service;
    private final List<String> resolveRoles;

    public ClaimController(ClaimService service,
                           @Value("${claims.resolve.allowed-roles:ADMIN}") List<String> resolveRoles) {
        this.service = service;
        this.resolveRoles = resolveRoles;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> create(@RequestHeader("X-User-Id") String userId,
                                      @Valid @RequestBody CreateClaimRequest req) {
        return service.create(userId, req);
    }

    @PostMapping("/{id}/otp/verify")
    public Claim verify(@PathVariable String id,
                        @RequestHeader("X-User-Id") String userId,
                        @Valid @RequestBody OtpVerifyRequest req) {
        return service.verifyOtp(id, userId, req.code());
    }

    @PostMapping("/{id}/otp/resend")
    public Map<String, String> resend(@PathVariable String id,
                                      @RequestHeader("X-User-Id") String userId) {
        return service.resendOtp(id, userId);
    }

    @GetMapping("/me")
    public List<Claim> mine(@RequestHeader("X-User-Id") String userId) {
        return service.mine(userId);
    }

    @GetMapping("/{id}")
    public Claim get(@PathVariable String id) { return service.get(id); }

    @GetMapping("/report/{reportId}")
    public List<Claim> byReport(@PathVariable String reportId) { return service.byReport(reportId); }

    @PatchMapping("/{id}/resolve")
    public Claim resolve(@PathVariable String id,
                         @RequestHeader("X-User-Role") String role,
                         @RequestBody ResolveClaimRequest req) {
        boolean allowed = resolveRoles.stream().anyMatch(r -> r.trim().equalsIgnoreCase(role));
        if (!allowed) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Tu rol no puede resolver reclamos");
        }
        return service.resolve(id, req.status());
    }
}