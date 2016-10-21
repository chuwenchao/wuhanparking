package com.mgear.wuhanparking.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mgear.wuhanparking.redis.TestRedis;
import com.mgear.wuhanparking.service.TestService;

public class TestServiceImpl implements TestService {
	
	@Autowired
	private TestRedis tr;
	
	@Override
	public String getList(String datas) {
		JSONObject param = JSON.parseObject(datas);
		JSONArray ja = param.getJSONArray("pushdata");
		tr.getList(ja);
		return datas;
	}

	@Override
	public String add(String datas) {
		System.out.println("datas: " + datas);
		int a = 1/1;
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "执行成功！";
	}

}
