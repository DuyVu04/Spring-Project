package com.example.spring_project.strategies;

import com.example.spring_project.service.PaymentService;
import org.springframework.stereotype.Component;

@Component("creditCard")
public class CreditPayment  implements PaymentService {
    @Override
    public void pay(double amount) {
        System.out.println("Paid " + amount + " using Credit Card.");
    }
}
