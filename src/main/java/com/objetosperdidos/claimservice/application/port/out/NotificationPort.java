package com.objetosperdidos.claimservice.application.port.out;

import com.objetosperdidos.claimservice.domain.model.Claim;

public interface NotificationPort {
    void sendOtp(String phone, String code);
    void sendApproval(Claim claim);
    void sendRejection(Claim claim);
}