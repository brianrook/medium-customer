package com.brianrook.medium.customer.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    ObjectMapper om = new ObjectMapper();

    @Around("execution(* com.brianrook.medium.customer..*(..)) " +
            "&& !within(com.brianrook.medium.customer.config. .*)" +
            "&& !within(com.brianrook.medium.customer.config.CustomerControllerAdvice)) ")
    public Object profileAllMethods(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        logMethodInvocationAndParameters(proceedingJoinPoint);

        final StopWatch stopWatch = new StopWatch();

        //Measure method execution time
        stopWatch.start();
        Object result = proceedingJoinPoint.proceed();
        stopWatch.stop();

        //Log method execution time
        logMethodResultAndParameters(proceedingJoinPoint, result, stopWatch.getTotalTimeMillis());

        return result;
    }

    private void logMethodResultAndParameters(ProceedingJoinPoint proceedingJoinPoint,
                                              Object result, long totalTimeMillis) {
        try {
            MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
            String className = methodSignature.getDeclaringType().getSimpleName();
            String methodName = methodSignature.getName();
            ObjectWriter writer = om.writer();
            if (writer != null) {
                log.info("<- {}.{} returns:{}.  Execution time: {}ms",
                        className,
                        methodName,
                        writer.writeValueAsString(result),
                        totalTimeMillis);
            }
        } catch (JsonProcessingException e) {
            log.error("unable to write log value: {}", e.getMessage(), e);
        }
    }


    private void logMethodInvocationAndParameters(ProceedingJoinPoint jp) {
        try {
            String[] argNames = ((MethodSignature) jp.getSignature()).getParameterNames();
            Object[] values = jp.getArgs();
            Map<String, Object> params = new HashMap<>();
            if (argNames!=null && argNames.length != 0) {
                for (int i = 0; i < argNames.length; i++) {
                    params.put(argNames[i], values[i]);
                }
            }
            ObjectWriter writer = om.writer();
            if (writer!=null) {
                String className = jp.getSignature().getDeclaringType().getSimpleName();
                String methodName = jp.getSignature().getName();
                log.info("-> {}.{} invocation.  params: {}",
                        className,
                        methodName,
                        writer.writeValueAsString(params));
            }
        } catch (JsonProcessingException e) {
            log.error("unable to write log value: {}", e.getMessage(), e);
        }

    }
}
