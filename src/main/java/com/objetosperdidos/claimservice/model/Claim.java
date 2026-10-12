package com.objetosperdidos.claimservice.model;


import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@Builder 
@NoArgsConstructor 
@AllArgsConstructor 
@Document(collation = "claims")

public class Claim {
    @Id 
    private String id;

    @Indexed 
    private String reportId;
    
    @Indexed 
    private String claimantId;

    private String description;
    private String contactPhone;

    private ClaimStatus status;
    private int fraudScore;
    private String fraudReason;

    private Instant createdAt;
    private Instant resolvedAt;
    
}
