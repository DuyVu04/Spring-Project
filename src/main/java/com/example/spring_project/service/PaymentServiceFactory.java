package com.example.spring_project.service;


import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
@Primary
public class PaymentServiceFactory implements FactoryBean<PaymentService> {

    private final ApplicationContext context;
    private final String method;

    public PaymentServiceFactory(ApplicationContext context,
                                 @Value("${payment.method}") String method) {
        this.context = context;
        this.method = method;
    }

    @Override
    public PaymentService getObject() {
        return (PaymentService) context.getBean(method);
    }

    @Override
    public Class<?> getObjectType() {
        return PaymentService.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
