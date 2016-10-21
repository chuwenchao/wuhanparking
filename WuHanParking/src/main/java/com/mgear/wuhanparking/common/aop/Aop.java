package com.mgear.wuhanparking.common.aop;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;


@Aspect
@Component
public class Aop implements Ordered{
	public static final String PackageName = "com.mgear.wuhanparking.service";
	public static final String EDP = "execution ( * " + PackageName + ".*.*(..) )";
	public static final Logger logEx = LoggerFactory.getLogger("Exception");
	public static final Logger logSy = LoggerFactory.getLogger(Aop.class);
	
	@Around(EDP)
	public Object logAndValidateAround(ProceedingJoinPoint joinPoint) {
		Signature sign = joinPoint.getSignature();
		String methodName = sign.getDeclaringTypeName().replace(PackageName + ".", "") + "." + sign.getName();
		
		Object args = joinPoint.getArgs()[0];
		Object obj = null;
		//参数校验
		String rlt = ValidateUtil.validate(methodName, args);
		if(!rlt.equals("1")){
			return rlt;
		}
		//前置通知日志
		logSy.info("服务名：" + methodName + " -- " +"输入参数：" + args);
		try{
			long b = System.currentTimeMillis();
			obj = joinPoint.proceed();
			//返回通知日志
			logSy.info("执行时间：" + (System.currentTimeMillis() - b) + " ms");
			if(obj != null){
				logSy.info("输出：" + obj.toString());
			}
		} catch(Throwable e) {
			logEx.error("服务名：" + methodName + " -- 输入参数：" + args);
			StringWriter trace = new StringWriter();
			e.printStackTrace(new PrintWriter(trace));
			logEx.error(trace.toString());
			return "{\"result\":\"0\",\"info\":\"系统错误，请联系管理员！\"}".intern();
		}
		return obj;
	}

	/**
	 * 设置切面优先级，值越小优先级越高
	 */
	@Override
	public int getOrder() {
		return 1;
	}
}

