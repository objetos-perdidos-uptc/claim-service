package com.objetosperdidos.claimservice.application.saga;

import com.objetosperdidos.claimservice.application.port.out.EventPublisherPort;
import com.objetosperdidos.claimservice.application.port.out.NotificationPort;
import com.objetosperdidos.claimservice.application.port.out.OtpPort;
import com.objetosperdidos.claimservice.domain.event.*;
import com.objetosperdidos.claimservice.domain.exception.NotificationFailedException;
import com.objetosperdidos.claimservice.domain.model.Claim;
import com.objetosperdidos.claimservice.domain.port.out.ClaimRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Instant;


@Component
public class ClaimSaga {

    private static final Logger log = LoggerFactory.getLogger(ClaimSaga.class);

    private final ClaimRepositoryPort repo;
    private final OtpPort otp;
    private final NotificationPort notifier;
    private final EventPublisherPort events;

    public ClaimSaga(ClaimRepositoryPort repo, OtpPort otp,
                     NotificationPort notifier, EventPublisherPort events) {
        this.repo = repo;
        this.otp = otp;
        this.notifier = notifier;
        this.events = events;
    }

    public String startCreation(Claim claim) {
        Claim saved = repo.save(claim);
        events.publish(new ClaimCreatedEvent(
                saved.getId().value(), saved.getReportId(),
                saved.getClaimantId(), saved.getContactPhone(), Instant.now()));

        String code = otp.generate(saved.getId().value());
        try {
            notifier.sendOtp(saved.getContactPhone(), code);
        } catch (RuntimeException ex) {
            log.error("Fallo enviando OTP para claim {}, compensando…", saved.getId(), ex);
            compensateCreation(saved, ex);
            throw new NotificationFailedException("No se pudo enviar el OTP", ex);
        }

        events.publish(new OtpDispatchedEvent(saved.getId().value(), Instant.now()));
        return code;
    }

    private void compensateCreation(Claim claim, RuntimeException cause) {
        try {
            claim.cancel("No se pudo enviar el OTP: " + cause.getMessage());
            repo.save(claim);
            events.publish(new ClaimCancelledEvent(
                    claim.getId().value(), claim.getCancellationReason(), Instant.now()));
        } catch (RuntimeException compensationError) {
            log.error("Compensación falló para claim {}", claim.getId(), compensationError);
        }
    }

    public void onOtpVerified(Claim claim) {
        Claim saved = repo.save(claim);
        events.publish(new OtpVerifiedEvent(saved.getId().value(), Instant.now()));
        log.info("OTP verificado para claim {} → estado {}", saved.getId(), saved.getStatus());
    }

    public void onResolved(Claim claim) {
        try {
            switch (claim.getStatus()) {
                case APPROVED -> {
                    notifier.sendApproval(claim);
                    events.publish(new ClaimApprovedEvent(
                            claim.getId().value(), claim.getPickupCode(), Instant.now()));
                }
                case REJECTED -> {
                    notifier.sendRejection(claim);
                    events.publish(new ClaimRejectedEvent(
                            claim.getId().value(), Instant.now()));
                }
                default -> log.warn("onResolved llamado con estado {}", claim.getStatus());
            }
        } catch (RuntimeException ex) {
            log.error("Fallo notificando resolución del claim {}, se reintentará",
                    claim.getId(), ex);
        }
    }
}