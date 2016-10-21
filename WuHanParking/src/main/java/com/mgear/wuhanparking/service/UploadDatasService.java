package com.mgear.wuhanparking.service;

public interface UploadDatasService {
	/**上传泊位状态数据*/
	public String uploadberthstatus(String datas);
	
	/**推送心态数据*/
	public String pushheartbeat(String datas);

}
