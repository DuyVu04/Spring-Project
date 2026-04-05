package com.example.spring_project.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.example.spring_project.dto.response.ApiResponse;
import com.example.spring_project.enums.MembershipType;
import com.example.spring_project.entity.PaymentTransaction;
import com.example.spring_project.entity.User;
import com.example.spring_project.repository.PaymentTransactionRepository;
import com.example.spring_project.repository.UserRepository;
import com.example.spring_project.service.PayOSService;
import com.example.spring_project.service.MailService;
import vn.payos.model.v2.paymentRequests.PaymentLink;
import vn.payos.model.v2.paymentRequests.PaymentLinkStatus;
import vn.payos.model.webhooks.WebhookData;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.cache.CacheManager;

@RestController
@RequestMapping("/payment/payos")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PayOSService payOSService;
    private final UserRepository userRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final MailService mailService;
    private final CacheManager cacheManager;

    /**
     * Tạo link thanh toán cho gói Premium
     * Frontend gọi: POST /api/payment/payos/create-premium?plan=MONTHLY|YEARLY
     */
    @PostMapping("/create-premium")
    public ApiResponse<Map<String, Object>> createPremiumPayment(@RequestParam String plan) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Sinh orderCode ngẫu nhiên (PayOS yêu cầu orderCode > 0, duy nhất)
        Long orderCode = System.currentTimeMillis() % 1_000_000_000L;

        int amount;
        String description;
        if ("YEARLY".equalsIgnoreCase(plan)) {
            amount = 990000;
            description = "Premium 1 Nam - " + user.getUsername();
        } else {
            amount = 10000;
            description = "Premium 1 Thang - " + user.getUsername();
        }

        String checkoutUrl = payOSService.createPaymentLink(orderCode, amount, description);

        PaymentTransaction transaction = PaymentTransaction.builder()
                .orderCode(orderCode)
                .amount(amount)
                .description(description)
                .plan(plan.toUpperCase())
                .status("PENDING")
                .paymentMethod("PayOS")
                .user(user)
                .build();
        paymentTransactionRepository.save(transaction);
        log.info("Tạo transaction PENDING: orderCode={}, user={}, plan={}", orderCode, username, plan);

        return ApiResponse.<Map<String, Object>>builder()
                .result(Map.of(
                        "checkoutUrl", checkoutUrl,
                        "orderCode", orderCode,
                        "amount", amount,
                        "plan", plan.toUpperCase(),
                        "description", description
                ))
                .build();
    }

    /**
     * Tạo link thanh toán chung (giữ nguyên API cũ)
     */
    @PostMapping("/create")
    public ApiResponse<String> createPayment(@RequestParam Long orderCode, @RequestParam int amount) {
        String description = "Thanh toan don hang " + orderCode;
        String checkoutUrl = payOSService.createPaymentLink(orderCode, amount, description);

        return ApiResponse.<String>builder()
                .result(checkoutUrl)
                .build();
    }

    /**
     * Webhook PayOS gọi khi thanh toán thành công
     */
    @PostMapping("/webhook")
    public ResponseEntity<Map<String, Object>> handleWebhook(@RequestBody String webhookBody) {
        try {
            WebhookData data = payOSService.verifyWebhook(webhookBody);
            Long orderCode = data.getOrderCode();
            Long amount = data.getAmount();

            log.info("Webhook thanh toán thành công: orderCode={}, amount={}", orderCode, amount);

            paymentTransactionRepository.findByOrderCode(orderCode).ifPresent(tx -> {
                if ("PENDING".equals(tx.getStatus())) {
                    tx.setStatus("PAID");
                    tx.setPaidAt(Instant.now());
                    paymentTransactionRepository.save(tx);
                    log.info("Webhook: Cập nhật transaction {} -> PAID", orderCode);

                    if (tx.getPlan() != null && (data.getDescription() != null && data.getDescription().contains("Premium"))) {
                        User user = tx.getUser();
                        if (user != null) {
                            boolean isNewUpgrade = (user.getMembershipType() != MembershipType.PREMIUM);

                            Instant startFrom = (user.getMembershipExpiry() != null && user.getMembershipExpiry().isAfter(Instant.now()))
                                    ? user.getMembershipExpiry()
                                    : Instant.now();

                            if ("YEARLY".equalsIgnoreCase(tx.getPlan())) {
                                user.setMembershipExpiry(startFrom.plus(365, ChronoUnit.DAYS));
                            } else {
                                user.setMembershipExpiry(startFrom.plus(30, ChronoUnit.DAYS));
                            }

                            user.setMembershipType(MembershipType.PREMIUM);
                            userRepository.save(user);

                            evictUserCache(user.getUsername());
                            log.info("Nâng cấp user {} lên PREMIUM đến ngày {} (via webhook)", user.getUsername(), user.getMembershipExpiry());

                            if (isNewUpgrade) {
                                sendPremiumWelcomeEmail(user, tx);
                            }
                        }
                    }
                }
            });

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Webhook verified"
            ));
        } catch (Exception e) {
            log.error("Xử lý Webhook thất bại: ", e);
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Invalid Webhook payload"
            ));
        }
    }

    /**
     * Endpoint kiểm tra trạng thái thanh toán (frontend gọi sau khi redirect)
     * Chủ động gọi PayOS API để verify, nâng cấp user nếu đã thanh toán
     */
    @GetMapping("/check-status")
    public ApiResponse<Map<String, Object>> checkPaymentStatus(@RequestParam Long orderCode) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Map<String, Object> result = new HashMap<>();
        result.put("orderCode", orderCode);

        try {

            PaymentLink paymentLink = payOSService.getPaymentInfo(orderCode);
            PaymentLinkStatus payosStatus = paymentLink.getStatus();
            log.info("PayOS verify: orderCode={}, status={}", orderCode, payosStatus);


            paymentTransactionRepository.findByOrderCode(orderCode).ifPresent(tx -> {
                if (payosStatus == PaymentLinkStatus.PAID && "PENDING".equals(tx.getStatus())) {
                    tx.setStatus("PAID");
                    tx.setPaidAt(Instant.now());
                    paymentTransactionRepository.save(tx);
                    log.info("Cập nhật transaction {} -> PAID", orderCode);

                    boolean isNewUpgrade = (user.getMembershipType() != MembershipType.PREMIUM);

                    Instant startFrom = (user.getMembershipExpiry() != null && user.getMembershipExpiry().isAfter(Instant.now()))
                            ? user.getMembershipExpiry()
                            : Instant.now();

                    if ("YEARLY".equalsIgnoreCase(tx.getPlan())) {
                        user.setMembershipExpiry(startFrom.plus(365, ChronoUnit.DAYS));
                    } else {
                        user.setMembershipExpiry(startFrom.plus(30, ChronoUnit.DAYS));
                    }

                    user.setMembershipType(MembershipType.PREMIUM);
                    userRepository.save(user);
                    evictUserCache(username);
                    log.info("Nâng cấp user {} lên PREMIUM đến ngày {} (via check-status)", username, user.getMembershipExpiry());

                    if (isNewUpgrade) {
                        sendPremiumWelcomeEmail(user, tx);
                    }
                } else if (payosStatus == PaymentLinkStatus.CANCELLED && "PENDING".equals(tx.getStatus())) {
                    tx.setStatus("CANCELLED");
                    paymentTransactionRepository.save(tx);
                }
            });

            result.put("paymentStatus", payosStatus.name());
            result.put("membershipType", user.getMembershipType().name());
            result.put("isPremium", user.getMembershipType() == MembershipType.PREMIUM);

        } catch (Exception e) {
            log.error("Lỗi verify payment từ PayOS: orderCode={}", orderCode, e);
            result.put("paymentStatus", "UNKNOWN");
            result.put("membershipType", user.getMembershipType().name());
            result.put("isPremium", user.getMembershipType() == MembershipType.PREMIUM);
        }

        return ApiResponse.<Map<String, Object>>builder()
                .result(result)
                .build();
    }

    private void sendPremiumWelcomeEmail(User user, PaymentTransaction tx) {
        if (user.getEmail() == null || user.getEmail().isEmpty()) return;

        String planName = "YEARLY".equalsIgnoreCase(tx.getPlan()) ? "Premium 1 Năm" : "Premium 1 Tháng";
        String subject = "🎉 Chúc mừng bạn đã nâng cấp thành công gói " + planName;

        String content = String.format(
                "<!DOCTYPE html><html lang=\"vi\"><head><meta charset=\"UTF-8\"><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"><style>" +
                        "body { font-family: 'Georgia', serif; background-color: #F7F3EE; color: #3E3A35; padding: 20px; line-height: 1.6; }" +
                        ".container { max-width: 600px; margin: 0 auto; background: #FFF9F2; border: 1px solid #D6C9B8; border-radius: 12px; padding: 30px; box-shadow: 0 4px 6px rgba(0,0,0,0.05); }" +
                        ".header { text-align: center; border-bottom: 1px solid #D6C9B8; padding-bottom: 20px; margin-bottom: 20px; }" +
                        ".header h1 { color: #8B6F47; font-style: italic; margin: 0; }" +
                        ".content p { font-size: 16px; margin-bottom: 15px; }" +
                        ".plan-box { background-color: #EADFCF; border-left: 4px solid #8B6F47; padding: 15px; border-radius: 4px; margin: 20px 0; font-weight: bold; }" +
                        ".footer { text-align: center; font-size: 14px; color: #6F665A; margin-top: 30px; border-top: 1px solid #D6C9B8; padding-top: 20px; }" +
                        ".button { display: inline-block; background-color: #8B6F47; color: #FFFFFF; text-decoration: none; padding: 12px 24px; border-radius: 6px; font-weight: bold; margin-top: 10px; }" +
                        "</style></head><body><div class=\"container\"><div class=\"header\"><h1>Chào mừng đến với Scholar Premium 👑</h1></div>" +
                        "<div class=\"content\"><p>Chào <strong>%s</strong>,</p><p>Tuyệt vời! Thanh toán của bạn đã được xác nhận. Chúc mừng bạn đã chính thức trở thành học viên <strong>PREMIUM</strong> của hệ thống giáo dục Scholar.</p>" +
                        "<div class=\"plan-box\">Gói dịch vụ kích hoạt: %s<br/>Mã giao dịch: #%d<br/>Giá trị: %d VNĐ</div>" +
                        "<p>Bạn hiện đã có đặc quyền truy cập <strong>không giới hạn</strong> vào toàn bộ khoá học chất lượng cao, ưu tiên cập nhật lộ trình mới nhất, và hỗ trợ AI Scholar.</p>" +
                        "<div style=\"text-align: center; margin-top: 30px;\"><a href=\"http://localhost:3000/en/courses\" class=\"button\">Bắt đầu học ngay</a></div>" +
                        "</div><div class=\"footer\"><p>\"The excellence of scholarship is measured by the depth of investment.\"</p><p>Scholar Education &copy; 2026. Mọi thắc mắc xin liên hệ bộ phận hỗ trợ.</p></div></div></body></html>",
                user.getFirstName() != null ? user.getFirstName() : user.getUsername(), planName, tx.getOrderCode(), tx.getAmount()
        );

        new Thread(() -> {
            try {
                mailService.sendEmail(user.getEmail(), subject, content, null);
                log.info("Đã gửi email chúc mừng nâng cấp Premium tới {}", user.getEmail());
            } catch (Exception e) {
                log.error("Lỗi gửi email nâng cấp Premium cho user {}", user.getEmail(), e);
            }
        }).start();
    }

    /**
     * Evict cache users để /users/myInfo trả dữ liệu mới nhất sau khi upgrade
     */
    private void evictUserCache(String username) {
        try {
            var cache = cacheManager.getCache("users");
            if (cache != null) {
                cache.evict(username);
                log.info("Evicted user cache for: {}", username);
            }
        } catch (Exception e) {
            log.warn("Failed to evict user cache for {}: {}", username, e.getMessage());
        }
    }

    /**
     * Lấy lịch sử giao dịch của user hiện tại
     */
    @GetMapping("/transactions")
    public ApiResponse<List<Map<String, Object>>> getTransactionHistory() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<PaymentTransaction> transactions = paymentTransactionRepository.findByUserOrderByCreatedAtDesc(user);

        List<Map<String, Object>> txList = transactions.stream().map(tx -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", tx.getId());
            map.put("orderCode", tx.getOrderCode());
            map.put("amount", tx.getAmount());
            map.put("description", tx.getDescription());
            map.put("plan", tx.getPlan());
            map.put("status", tx.getStatus());
            map.put("paymentMethod", tx.getPaymentMethod());
            map.put("createdAt", tx.getCreatedAt() != null ? tx.getCreatedAt().toString() : null);
            map.put("paidAt", tx.getPaidAt() != null ? tx.getPaidAt().toString() : null);
            return map;
        }).toList();

        return ApiResponse.<List<Map<String, Object>>>builder()
                .result(txList)
                .build();
    }

    /**
     * Admin: Lấy toàn bộ lịch sử giao dịch
     */
    @GetMapping("/admin/transactions")
    public ApiResponse<List<Map<String, Object>>> getAllTransactions() {
        List<PaymentTransaction> transactions = paymentTransactionRepository.findAllByOrderByCreatedAtDesc();

        List<Map<String, Object>> txList = transactions.stream().map(tx -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", tx.getId());
            map.put("orderCode", tx.getOrderCode());
            map.put("amount", tx.getAmount());
            map.put("description", tx.getDescription());
            map.put("plan", tx.getPlan());
            map.put("status", tx.getStatus());
            map.put("paymentMethod", tx.getPaymentMethod());
            map.put("createdAt", tx.getCreatedAt() != null ? tx.getCreatedAt().toString() : null);
            map.put("paidAt", tx.getPaidAt() != null ? tx.getPaidAt().toString() : null);
            if (tx.getUser() != null) {
                map.put("username", tx.getUser().getUsername());
                map.put("email", tx.getUser().getEmail());
            }
            return map;
        }).toList();

        return ApiResponse.<List<Map<String, Object>>>builder()
                .result(txList)
                .build();
    }

    /**
     * Admin: Thống kê giao dịch (Doanh thu, Tỷ lệ, ...)
     */
    @GetMapping("/admin/transactions/stats")
    public ApiResponse<Map<String, Object>> getTransactionStats() {
        List<PaymentTransaction> allTx = paymentTransactionRepository.findAll();

        Instant now = Instant.now();
        // Lấy tháng hiện tại và tháng trước
        java.time.YearMonth currentMonth = java.time.YearMonth.now(java.time.ZoneId.of("Asia/Ho_Chi_Minh"));
        java.time.YearMonth lastMonth = currentMonth.minusMonths(1);

        long monthlyRevenue = 0;
        long lastMonthRevenue = 0;
        int pendingInvoices = 0;
        int paidCount = 0;

        for (PaymentTransaction tx : allTx) {
            String status = tx.getStatus();
            if ("PENDING".equalsIgnoreCase(status)) {
                pendingInvoices++;
            } else if ("PAID".equalsIgnoreCase(status)) {
                paidCount++;
                Instant timeToUse = tx.getPaidAt() != null ? tx.getPaidAt() : tx.getCreatedAt();
                if (timeToUse != null) {
                    java.time.YearMonth txMonth = java.time.YearMonth.from(timeToUse.atZone(java.time.ZoneId.of("Asia/Ho_Chi_Minh")));
                    if (txMonth.equals(currentMonth)) {
                        monthlyRevenue += tx.getAmount();
                    } else if (txMonth.equals(lastMonth)) {
                        lastMonthRevenue += tx.getAmount();
                    }
                }
            }
        }

        double revenueGrowth = 0.0;
        if (lastMonthRevenue == 0 && monthlyRevenue > 0) {
            revenueGrowth = 100.0;
        } else if (lastMonthRevenue > 0) {
            revenueGrowth = ((double) (monthlyRevenue - lastMonthRevenue) / lastMonthRevenue) * 100;
        }

        double premiumConversion = 0.0;
        if (!allTx.isEmpty()) {
            premiumConversion = ((double) paidCount / allTx.size()) * 100;
        }

        Map<String, Object> stats = new HashMap<>();
        stats.put("monthlyRevenue", monthlyRevenue);
        stats.put("revenueGrowth", Math.round(revenueGrowth * 10.0) / 10.0);
        stats.put("pendingInvoices", pendingInvoices);
        stats.put("premiumConversion", Math.round(premiumConversion * 10.0) / 10.0);

        return ApiResponse.<Map<String, Object>>builder()
                .result(stats)
                .build();
    }
}