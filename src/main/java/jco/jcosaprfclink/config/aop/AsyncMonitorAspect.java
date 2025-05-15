package jco.jcosaprfclink.config.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class AsyncMonitorAspect {
    @Around("@annotation(org.springframework.scheduling.annotation.Async)")
    public Object monitorAsync(ProceedingJoinPoint joinPoint) throws Throwable {
        String method = joinPoint.getSignature().toShortString();
        log.info("[모니터링] 비동기 작업 시작: {}", method);
        long start = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            log.info("[모니터링] 비동기 작업 정상 종료: {} ({}ms)", method, System.currentTimeMillis() - start);
            return result;
        } catch (Throwable ex) {
            log.error("[모니터링] 비동기 작업 예외 발생: {}", method, ex);
            throw ex;
        }
    }
} 