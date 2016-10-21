package com.mgear.wuhanparking.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mgear.wuhanparking.common.activemq.MessageSender;
import com.mgear.wuhanparking.dao.UploadDatasMapper;
import com.mgear.wuhanparking.service.UploadDatasService;
import com.mgear.wuhanparking.util.Consant;
import com.mgear.wuhanparking.util.FastJsonUtil;

public class UploadDatasServiceImpl implements UploadDatasService {
	
	@Autowired
	private UploadDatasMapper udm;
	
	@Autowired
	private MessageSender ms;

	@Override
	public String uploadberthstatus(String datas) {
		JSONObject param = FastJsonUtil.getParams(datas);
		JSONArray ja = param.getJSONArray("pushdata");
		//将其他数据存入数据库
		udm.saveBerthStatus(param);
		//将推送泊位数据放入队列(可考虑使用多线程)
		ms.sendMessage(ja.toString());
		
		
		return Consant.Receive_Success;
	}

	@Override
	public String pushheartbeat(String datas) {
		// TODO Auto-generated method stub
		return null;
	}

}
