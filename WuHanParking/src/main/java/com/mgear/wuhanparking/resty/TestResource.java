package com.mgear.wuhanparking.resty;

import com.mgear.wuhanparking.common.interceptor.SpringContextHelper;
import com.mgear.wuhanparking.service.TestService;

import cn.dreampie.route.core.annotation.API;
import cn.dreampie.route.core.annotation.GET;

@API("/Test")
public class TestResource extends ApiResource {
	
	private TestService ts = (TestService) SpringContextHelper.getBean("testService");

	@GET("/getList")
	public String getList(String appkey, String timestamp, String method, String pushdata, String sign) {
		System.out.println("appkey: " + appkey);
		System.out.println("timestamp: " + timestamp);
		System.out.println("method: " + method);
		System.out.println("sign: " + sign);
		return ts.getList(pushdata);
	}
	
}
