package com.mgear.wuhanparking.common.aop;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

//@Order(1)
//@Aspect
//@Component
public class LoggingAspect {
	
	private static long time = 0;
	private static final String PackageName = "com.mgear.wuhanparking.service";
	
	private static final Logger LogSy = LoggerFactory.getLogger(LoggingAspect.class);
	private static final Logger LogEx = LoggerFactory.getLogger("Exception");
	
	@Pointcut("execution (* com.mgear.wuhanparking.service.*.*(..))")
	public void declarePointCut(){}
	
	@Before("declarePointCut()")
	public void beforeMethodLog(JoinPoint joinPoint){
		Signature sign = joinPoint.getSignature();
		String methodName = sign.getDeclaringTypeName().replace(PackageName + ".", "") 
				+ "." + sign.getName();
		LogSy.info("服务名：" + methodName + " -- 参数：" + joinPoint.getArgs()[0]);
		time = System.currentTimeMillis();
	}
	
	@AfterReturning(value="declarePointCut()", returning="result")
	public void afterReturningLog(JoinPoint joinPoint, Object result) {
		LogSy.info("执行时间：" + (System.currentTimeMillis() - time) + " ms");
		LogSy.info("输出：" + result);
	}
	
	@AfterThrowing(value="declarePointCut()", throwing="e")
	public String afterThrowingLog(JoinPoint joinPoint, Exception e) {
		Signature sign = joinPoint.getSignature();
		String methodName = sign.getDeclaringTypeName().replace(PackageName + ".", "") 
				+ "." + sign.getName();
		LogEx.error("服务名：" + methodName + " -- 参数：" + joinPoint.getArgs()[0]);
		StringWriter trace = new StringWriter();
		e.printStackTrace(new PrintWriter(trace));
		LogEx.error(trace.toString());
		return "{\"result\":\"0\",\"info\":\"系统错误，请联系管理员！\"}";
	}

}
