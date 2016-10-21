package com.activemq.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mgear.wuhanparking.common.activemq.MessageSender;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/spring.xml", "/spring-redis.xml", "/spring-jms.xml", "/spring-mybatis.xml"})
public class SpringJmsTest {
	
	@Autowired
	private MessageSender messageSender;
	
	@Test
	public void test() {
		 for (int i=0; i<100; i++) {   
			 	messageSender.sendMessage("你好，生产者！这是消息：" + (i+1));   
	        }   
	}
	
}
