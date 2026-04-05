package com.example.spring_project.strategies;

import com.example.spring_project.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component("paypal")
public class PaypalPayment implements PaymentService {
    @Override
    public void pay(double amount) {
        String s1= "Paid " + amount + " using PayPal.";
        log.info(s1);
    }
}
