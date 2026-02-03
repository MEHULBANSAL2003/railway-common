package com.railway.common.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

  @Around("@annotation(Loggable) || @within(Loggable)")
  public Object logMethodExecution(ProceedingJoinPoint joinPoint) throws Throwable {
    String methodName = joinPoint.getSignature().getName();
    String className = joinPoint.getTarget().getClass().getSimpleName();

    log.info("→ {}.{} started", className, methodName);

    long start = System.currentTimeMillis();
    try {
      Object result = joinPoint.proceed();
      long duration = System.currentTimeMillis() - start;
      log.info("✓ {}.{} completed in {}ms", className, methodName, duration);
      return result;
    } catch (Exception e) {
      long duration = System.currentTimeMillis() - start;
      log.error("✗ {}.{} failed after {}ms: {}", className, methodName, duration, e.getMessage());
      throw e;
    }
  }
}
