package com.example.spring_project.service;

import com.example.spring_project.repository.PaymentTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionCleanupService {
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final PayOSService payOSService;

    @Scheduled(fixedRate = 600000)
    public void cleanupPendingTransactions() {
        log.info("Running scheduled cleanup of PENDING transactions...");
        Instant threshold = Instant.now().minus(30, ChronoUnit.MINUTES);
        var pendingTxs = paymentTransactionRepository.findByStatusAndCreatedAtBefore("PENDING", threshold);

        for (var tx : pendingTxs) {
            try {
                var info = payOSService.getPaymentInfo(tx.getOrderCode());
                if (info.getStatus() == vn.payos.model.v2.paymentRequests.PaymentLinkStatus.PAID) {
                    tx.setStatus("PAID");
                    tx.setPaidAt(Instant.now());
                    log.info("Transaction {} recovered to PAID after 30 mins", tx.getOrderCode());
                } else if (info.getStatus() == vn.payos.model.v2.paymentRequests.PaymentLinkStatus.CANCELLED) {
                    tx.setStatus("CANCELLED");
                    log.info("Transaction {} was CANCELLED via PayOS", tx.getOrderCode());
                } else {
                    tx.setStatus("CANCELLED");
                    log.info("Transaction {} automatically CANCELLED after 30 minutes threshold", tx.getOrderCode());
                }
            } catch (Exception e) {
                tx.setStatus("CANCELLED");
                log.info("Transaction {} CANCELLED (PayOS check failed)", tx.getOrderCode());
            }
            paymentTransactionRepository.save(tx);
        }
    }
}
