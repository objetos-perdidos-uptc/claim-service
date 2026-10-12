package com.objetosperdidos.claimservice.controller;

import org.springframework.web.bind.annotation.RestController;

import com.objetosperdidos.claimservice.model.ClaimStatus;
import com.objetosperdidos.claimservice.repository.ClaimRepository;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;


@RestController 
public class MetricsController {
    
    private final ClaimRepository repository;

    public  MetricsController(ClaimRepository repository){
        this.repository = repository;
    }

    @GetMapping("/claims/metrics")
    public Map<String, Object> claimMetrics() {
        Map<String, Object> m = new LinkedHashMap<>();
        long total = repository.count();
        long approved = repository.countByStatus(ClaimStatus.APPROVED);
        long rejected = repository.countByStatus(ClaimStatus.REJECTED);

        m.put("total", total);
        for (ClaimStatus s : ClaimStatus.values()) {
            m.put(s.name().toLowerCase(), repository.countByStatus(s));
        }
        long resolved = approved + rejected;
        m.put("approvalRate", resolved== 0 ? 0.0 : Math.round(approved * 1000.0 / resolved)/ 10.0);
        m.put("fraudRate", total == 0 ? 0.0 : Math.round(repository.countByStatus(ClaimStatus.FLAGGED) * 1000.0 /total)/10.0);
        return m;
    }
    
}
