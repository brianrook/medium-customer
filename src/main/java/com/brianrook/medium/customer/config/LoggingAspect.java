package com.brianrook.medium.customer.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    ObjectMapper om = new ObjectMapper();

    @Around("execution(* com.brianrook.medium.customer..*(..)))")
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
            String methodName = methodSignature.getName();
            ObjectWriter writer = om.writerWithDefaultPrettyPrinter();
            log.info("\n<- {} returns \n{}}\nExecution time: {}ms",
                    methodName,
                    writer.writeValueAsString(result),
                    totalTimeMillis);
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

            log.info("-> method " + jp.getSignature().getName() + " invocation", true);

            ObjectWriter writer = om.writerWithDefaultPrettyPrinter();
            if (!params.isEmpty()) log.info(writer.writeValueAsString(params));
        } catch (JsonProcessingException e) {
            log.error("unable to write log value: {}", e.getMessage(), e);
        }

    }
}
