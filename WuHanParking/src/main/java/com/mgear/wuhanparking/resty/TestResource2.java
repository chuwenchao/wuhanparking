package com.mgear.wuhanparking.resty;

import com.mgear.wuhanparking.common.interceptor.SpringContextHelper;
import com.mgear.wuhanparking.service.TestService;

import cn.dreampie.route.core.Resource;
import cn.dreampie.route.core.annotation.API;
import cn.dreampie.route.core.annotation.GET;

@API("/API")
public class TestResource2 extends Resource {

private TestService m = (TestService) SpringContextHelper.getBean("testService");
	
	@GET("/getList/:datas")
	public String getList(String datas) {
		return m.getList(datas);
	}
}
