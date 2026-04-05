package com.example.spring_project.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import vn.payos.PayOS;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkRequest;
import vn.payos.model.v2.paymentRequests.PaymentLinkItem;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkResponse;
import vn.payos.model.v2.paymentRequests.PaymentLink;
import vn.payos.model.webhooks.WebhookData;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PayOSService {

    private final PayOS payOS;

    @Value("${payos.return-url}")
    private String returnUrl;

    @Value("${payos.cancel-url}")
    private String cancelUrl;


    public String createPaymentLink(Long orderCode, int amount, String description) {
        try {
            PaymentLinkItem item = PaymentLinkItem.builder()
                    .name(description)
                    .quantity(1)
                    .price((long) amount)
                    .build();

            CreatePaymentLinkRequest requestData = CreatePaymentLinkRequest.builder()
                    .orderCode(orderCode)
                    .amount((long) amount)
                    .description(description)
                    .returnUrl(returnUrl)
                    .cancelUrl(cancelUrl)
                    .items(List.of(item))
                    .build();

            CreatePaymentLinkResponse response = payOS.paymentRequests().create(requestData);


            return response.getCheckoutUrl();

        } catch (Exception e) {
            log.error("Lỗi khi tạo link thanh toán PayOS: ", e);
            throw new RuntimeException("Không thể tạo phiên thanh toán PayOS");
        }
    }


    public PaymentLink getPaymentInfo(Long orderCode) {
        try {
            PaymentLink data = payOS.paymentRequests().get(orderCode);
            log.info("PayOS payment info: orderCode={}, status={}, amount={}",
                    data.getOrderCode(), data.getStatus(), data.getAmount());
            return data;
        } catch (Exception e) {
            log.error("Lỗi khi lấy thông tin thanh toán PayOS: orderCode={}", orderCode, e);
            throw new RuntimeException("Không thể xác thực thanh toán từ PayOS");
        }
    }


    public WebhookData verifyWebhook(String webhookBody) {
        try {
            return payOS.webhooks().verify(webhookBody);
        } catch (Exception e) {
            log.error("Xác thực Webhook thất bại: ", e);
            throw new RuntimeException("Dữ liệu Webhook không hợp lệ");
        }
    }
}