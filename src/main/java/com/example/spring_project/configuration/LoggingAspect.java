package com.example.spring_project.configuration;


import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {
    @Before("execution(* com.example.spring_project.service.*.*(..))")
    public void logBefore(JoinPoint joinPoint) {
        System.out.println("Before method: " + joinPoint.getSignature().getName()
                + " | Args: " + java.util.Arrays.toString(joinPoint.getArgs()));
    }

    @AfterReturning("execution(* com.example.spring_project.service.*.*(..))")
    public void logAfter(JoinPoint joinPoint) {
        System.out.println("After method: " + joinPoint.getSignature().getName());
    }

    @Around("execution(* com.example.spring_project.service.*.*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();


        Object result = joinPoint.proceed();

        long end = System.currentTimeMillis();
        System.out.println("Method: " + joinPoint.getSignature().getName()
                + " executed in " + (end - start) + " ms");
        return result;
    }
}
