package com.mgear.wuhanparking.common.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

//@Order(2)
//@Aspect
//@Component
public class ValidateAspect {
	
	private static Logger LogSy = LoggerFactory.getLogger(ValidateAspect.class);

	@Before("com.mgear.wuhanparking.common.aop.LoggingAspect.declarePointCut()")
	public void beforeMethodValidate(JoinPoint joinPoint) {
		JSONObject param = JSON.parseObject((String)joinPoint.getArgs()[0]);
		ValidateUtil.removeNull(param);
		System.out.println("param --" + param);
		System.out.println("joinPoint.getArgs()[0] --" + joinPoint.getArgs()[0]);
		LogSy.info("校验结果：成功！");
	}

}
