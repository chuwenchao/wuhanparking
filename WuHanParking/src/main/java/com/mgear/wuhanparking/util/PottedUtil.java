package com.mgear.wuhanparking.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.alibaba.fastjson.JSONObject;
import com.mgear.wuhanparking.common.interceptor.DBTYPE;
import com.mgear.wuhanparking.common.interceptor.PageInterceptor;

/**
 * DRY原则：抽取通用代码
 * @author chuwc
 * @created 2016-08-15
 * @update
 */
public class PottedUtil {
	
	/**
     * 启动分页
     * @author chuwc 2016-08-15
     * @param param 
     * @param type 数据库类型
     * @throws Exception 输入分页参数异常导致报错
     */
    public static void beginPaging(JSONObject param, DBTYPE type) throws Exception{
		Integer CurrentPage = param.getInteger("CurrentPage");
		Integer PageRecord = param.getInteger("PageRecord");
		PageInterceptor.startPage(CurrentPage, PageRecord, type);
    }
    
    /**
     * 日期处理
     * @author chuwc 2016-08-15
     * @note Jdk 1.7 switch case 才支持字符串用法
     * @return
     */
    public static void setTime(JSONObject param, String[] str){
    	if(param.containsKey(str[0])) {
    		Date d = new Date();
    		switch (param.getString(str[0])) {
				case "0":  //本日
					param.put(str[1], DateUtil.dateStr4(DateUtil.getFirstTimeOfNow(d)));
					param.put(str[2], DateUtil.dateStr4(DateUtil.getLastTimeOfNow(d)));
					break;
				case "1":  //本周
					param.put(str[1], DateUtil.getMondayOFWeek(d) + " 00:00:00");
					param.put(str[2], DateUtil.getCurrentWeekday(d) + " 23:59:59");
					break;
				case "2":  //本月
					param.put(str[1], DateUtil.getFirstDayOfMonth());
					param.put(str[2], DateUtil.getLastDayOfMonth());
					break;
				default:  //自定义
					SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd");
					Date t;
					if(param.containsKey(str[1])){
						try {
							t = formatter.parse(param.getString(str[1]));
							param.put(str[1], DateUtil.dateStr4(DateUtil.getFirstTimeOfNow(t)));
						} catch (ParseException e) {
							e.printStackTrace();
						}	
					}
					if(param.containsKey(str[2])){
						try {
							t = formatter.parse(param.getString(str[2]));
							param.put(str[2], DateUtil.dateStr4(DateUtil.getLastTimeOfNow(t)));
						} catch (ParseException e) {
							e.printStackTrace();
						}	
					}
					break;
			}
    	}
    }
    
    /**
     * 日期处理：前n天
     * @param param
     * @param str
     */
    public static void setTime2(JSONObject param, String[] str){
    	if(param.containsKey(str[0])) {
    		Date date = new Date();
    		int n = Integer.valueOf(param.getString(str[0]));
    		param.put(str[1], DateUtil.dateStr4(DateUtil.getBeforeNDay(date, n, true)));
			param.put(str[2], DateUtil.dateStr4(DateUtil.getBeforeNDay(date, n, false)));
    	}
    }
    
//    public static void main(String[] args) {
//		JSONObject param = new JSONObject();
//		param.put("Name", "chuwc");
//		param.put("TimeType", "3");
//		param.put("StartTime", "2016-08-1");
//		param.put("EndTime", "2016-08-3");
//		setTime(param, new String[] {"TimeType", "StartTime", "EndTime"});
//		System.out.println(param);
//	}

}
