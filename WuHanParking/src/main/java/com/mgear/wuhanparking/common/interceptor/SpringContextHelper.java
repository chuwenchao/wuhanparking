package com.mgear.wuhanparking.common.interceptor;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;



public class SpringContextHelper implements ApplicationContextAware {
	
	private static ApplicationContext context;
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		// TODO Auto-generated method stub
		context = applicationContext;
	}
	
	public static Object getBean(String beanName) {
		return context.getBean(beanName);
	}
}
