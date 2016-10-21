package com.service.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mgear.wuhanparking.common.interceptor.SpringContextHelper;
import com.mgear.wuhanparking.service.TestService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations= {"/spring.xml", "/spring-redis.xml", "/spring-jms.xml", "/spring-mybatis.xml"})
public class ServiceTest {
	
	@Autowired
	private TestService ts;
	
	@Test
	public void test() {
		System.out.println(ts.add("{\"table\":{\"tableName\":\"Pub_Query\",\"row\":{\"CurrentPage\":\"1\",\"PageRecord\":\"10\",\"DateTime\":\"2016-8-15 3:60\"}}}"));
	}
	
}
