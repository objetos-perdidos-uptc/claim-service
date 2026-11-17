package com.objetosperdidos.claimservice.infrastructure.in.web;

import com.objetosperdidos.claimservice.application.dto.ClaimMetrics;
import com.objetosperdidos.claimservice.application.port.in.GetMetricsUseCase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class MetricsController {

    private final GetMetricsUseCase uc;

    public MetricsController(GetMetricsUseCase uc) { this.uc = uc; }

    @GetMapping("/claims/metrics")
    public Map<String, Object> metrics() {
        ClaimMetrics m = uc.get();
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("total", m.total());
        out.put("pending_otp", m.pendingOtp());
        out.put("pending", m.pending());
        out.put("approved", m.approved());
        out.put("rejected", m.rejected());
        out.put("flagged", m.flagged());
        out.put("approvalRate", m.approvalRate());
        out.put("fraudRate", m.fraudRate());
        return out;
    }
}