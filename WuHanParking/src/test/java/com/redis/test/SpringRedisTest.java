package com.redis.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mgear.wuhanparking.redis.TestRedis;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/spring.xml", "/spring-redis.xml", "/spring-jms.xml", "/spring-mybatis.xml"})
public class SpringRedisTest {
	@Autowired
	private TestRedis tr;
	
	@Test
	public void test() {
		System.out.println(tr.add());
	}
}
