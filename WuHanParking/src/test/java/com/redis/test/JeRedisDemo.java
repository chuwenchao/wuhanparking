package com.redis.test;

import java.util.HashMap;
import java.util.Map;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

public class JeRedisDemo {

	public static void main(String[] args) {
		Jedis jedis = new Jedis("192.168.0.24");
		jedis.auth("epark");
		if(!"PONG".equals(jedis.ping())) {
			System.out.println("连接redis失败！");
		} else {
			jedis.hset("map", "name", "chuwc");
			jedis.hset("map", "age", "17");
			System.out.println(jedis.hgetAll("map"));
			
			//使用管道写（速度更快）
			Pipeline p = jedis.pipelined();
			Map<String, String> map = new HashMap<String, String>();
			for(int i=0;i<5;i++){
				map.put("K_"+i, "V_"+i);
				p.hmset("map_"+i, map);
			}
			p.sync();
			//使用管道读（速度更快）
			Map<String,Map<String,String>> result = new HashMap<String,Map<String,String>>();
			Map<String, Response<Map<String, String>>> response = new HashMap<String, Response<Map<String,String>>>();
			for(int i=0;i<5;i++){
				response.put("map_"+i, p.hgetAll("map_"+i));
			}
			p.sync();
			for(String k : response.keySet()) {
				result.put(k, response.get(k).get());
			}
			System.out.println(result.toString());
		}
	}
}
