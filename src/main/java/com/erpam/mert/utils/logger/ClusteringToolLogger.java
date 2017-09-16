package com.erpam.mert.utils.logger;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class ClusteringToolLogger {

    @Pointcut("execution(* com.erpam.mert.application.ClusteringTool.*(..))")
    public void clusteringToolMethods() {
    }

    @Around("clusteringToolMethods()")
    public Object profile(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.currentTimeMillis();
        Object output = pjp.proceed();
        long elapsedTime = System.currentTimeMillis() - start;
        System.out.println(pjp.getSignature().getName() + " execution time: " + elapsedTime / 1000 + " seconds.");
        return output;
    }
}
