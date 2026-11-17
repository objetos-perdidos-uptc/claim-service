package com.objetosperdidos.claimservice.infrastructure.out.notification;

import com.objetosperdidos.claimservice.application.port.out.NotificationPort;
import com.objetosperdidos.claimservice.domain.model.Claim;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LoggingNotificationAdapter implements NotificationPort {

    private static final Logger log = LoggerFactory.getLogger(LoggingNotificationAdapter.class);

    @Override
    public void sendOtp(String phone, String code) {
        log.info("[NOTIFY] OTP a {}: {}", phone, code);
    }

    @Override
    public void sendApproval(Claim claim) {
        log.info("[NOTIFY] Reclamo {} APROBADO · pickupCode={}",
                claim.getId().value(), claim.getPickupCode());
    }

    @Override
    public void sendRejection(Claim claim) {
        log.info("[NOTIFY] Reclamo {} RECHAZADO", claim.getId().value());
    }
}