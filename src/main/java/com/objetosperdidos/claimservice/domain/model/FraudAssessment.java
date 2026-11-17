package com.objetosperdidos.claimservice.domain.model;

public record FraudAssessment(int score, String reason, boolean suspicious) {
    
    public  FraudAssessment{
        if (score < 0 || score > 100) {
            throw new IllegalArgumentException("Score debe estar entre 0 y 100, llegó:" + score);
        }
    }

    public static FraudAssessment clean(){
        return  new FraudAssessment(0, "", false);
    }
}
