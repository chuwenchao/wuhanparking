package com.mgear.wuhanparking.common.aop;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.regex.Pattern;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;


public class ValidateUtil {
	private static HashMap<String,String> regular = new HashMap<String, String>();
	private static HashMap<String,String> service = new HashMap<String, String>();
	
	//加载默认校验格式
	static {
		 //中文, 电话,email,身份证格式1,身份证格式2,日期, 时间,数字,整数,金额  //INT_X_Y 
//	    String keys[]={"CHINESS","PHONE","EMAIL","IDCARD1","IDCARD2","DATE","TIME","NUMBER","INT","AMOUNT"};
//	    String values[]={"^[\\u4e00-\\u9fa5]{2,25}$","^((13[0-9])|(15[^4,\\D])|(18[0-9]))\\d{8}$","^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$","^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$","^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9]|X)$","^\\d{4}-[0-1]\\d-[0-3]\\d$","^\\d{4}-[0-1]\\d-[0-3]\\d [0-2]\\d:[0-5]\\d$","^\\d+(.\\d+)?$",  "^\\d+$","^(([1-9]\\d{0,7}))((\\.[0-9]{1,2})?)$"
//	    };
//	    //将校验规则放入 regular
//	    for(int i=0; i<keys.length; i++){
//	    	regular.put(keys[i], values[i]);
//	    }
	    //将接口参数校验信息放入 service
	    addProperties("validateInterface.properties",service);
	    //将校验规则放入 regular
	    addProperties("regular.properties",regular);
//	    System.out.println(regular);
	}
	
	static String validate(String methodname, Object datas){
		if( !(datas instanceof String)){
			return "输入参数不是字符串类型.";
		}
		JSONObject json = null;
		try {
			json = JSON.parseObject(datas.toString());
		} catch (Exception e) {
			return getError("JSON格式错误.");
		}
		//传入参数去空: 不会影响实际传给方法的参数，因为在转为JSON的时候对象的引用发生改变，即JSON实现对了对parse对象的复制
		removeNull(json);
		//参数值校验
//		if(methodname.indexOf("TestService") == -1) {  //排除特殊接口
		String rlt = validateValue(json, methodname);
		if(!rlt.equals("1")){
			return rlt;
		}
//		}
		return "1";
	}
	
	public static String getError(String info){
		return "{\"result\":\"0\",\"info\":\""+info+"\"}";
	}
	
	public static void removeNull(Object obj){
		if(obj instanceof JSONObject){
			JSONObject json = (JSONObject) obj;
			Iterator<String> keys = ((JSONObject) obj).keySet().iterator();
			while(keys.hasNext()){
				String key = keys.next();
				if(isEmptyOrWhitespaceOnly(json.getString(key))){
					keys.remove();
				}else{
					removeNull(json.get(key));
				}
			}
		}else if(obj instanceof JSONArray) {
			Iterator<Object> keys = ((JSONArray) obj).iterator();
			while(keys.hasNext()){
				removeNull(keys.next());
			}
		}
	}
	
	public static boolean isEmptyOrWhitespaceOnly(String str){
		if(str == null || str.length() == 0){
			return true;
		}
		int length = str.length();
		for(int i = 0; i < length; i++){
			if(!Character.isWhitespace(str.charAt(i))){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 参数值校验
	 * @param json
	 * @param methodname
	 * @return
	 */
	public static String validateValue(JSONObject json, String methodname) {
		try {
			json = json.getJSONObject("table").getJSONObject("row");
		} catch (JSONException e) {
			return getError("json格式不符合约定规范.");
		}
		String rlt = "1";
		//该接口是否需要校验
		if (!service.containsKey(methodname)) { //无需校验
			return rlt;	
		}
		String line = service.get(methodname);     //获取UserType,INT_1_3;RelationId;StartDate,DATE;StartTime,TIME;CashAmount,NUMBER;Name,CHINESS;Idcard,IDCARD
		String args[] = line.split(";");
		int size = args.length;
		for(int i=0; i<size; i++){
			String arg[] = args[i].split(",");
			if (!json.containsKey(arg[0])){ //必填项校验
				return getError("没有必填项 ："+arg[0]);
			}else if (arg.length == 2){ // 值校验
				if(arg[1].contains("INT_")){ //整数校验
					String rs = validateInt(json, arg);
					if(rs != null){
						return rs;
					}
				} else if(arg[1].equals("IDCARD")){  //身份证校验
					if (! (Pattern.matches( regular.get("IDCARD1"), json.getString(arg[0])) || Pattern.matches( regular.get("IDCARD2"), json.getString(arg[0])) )) 
						return getError(arg[0]+" 不符合 "+arg[1]+ " 格式的要求.");
				} else{ //获取该字段要求的正则表达式
					if (!regular.containsKey(arg[1])) return getError("无效校验条件，没有该类型对应的正则表达式.");
					if (!Pattern.matches( regular.get(arg[1]), json.getString(arg[0]))) 
						return getError(arg[0]+" 不符合 "+arg[1]+ " 格式的要求.");
				}
			}
		}
		return "1";
	}
	
	/**
	 * 整数校验
	 * @return
	 */
	private static String validateInt(JSONObject json, String[] arg){
		String result = null;
		int x = -1;
		try{
			x = json.getIntValue(arg[0]);
		}catch(JSONException e){
			result = ValidateUtil.getError(arg[0]+" 的值应为整数.");
		}
		String intargs[] = arg[1].split("_");
		if (intargs.length == 2 ){
			if (x < Integer.parseInt(intargs[1]) )
				result = ValidateUtil.getError(arg[0]+" 的值不在给定的范围内.");
		}else{
			if (x < Integer.parseInt(intargs[1]) || x > Integer.parseInt(intargs[2]))
				result = ValidateUtil.getError(arg[0]+" 的值不在给定的范围内.");
		}
		return result;
	}
	
//	static String getPattern(String key){
//		if (regular.containsKey(key)){
//			return regular.get(key);
//		}else{
//			return "无此格式";
//		}
//	}
	
	//加载服务名 和对应的字段格式
//	static String addService(String path){
//		return addProperties(path, service);
//	}
//
//	static String addPattern(String path){
//		return addProperties(path, regular);
//	}
		
	private static String addProperties(String path, HashMap<String,String> m){
		//1 是否有这个properties 文件
		//2 是否重名
		//3 放入map
		InputStream in = ValidateUtil.class.getResourceAsStream(path); //不以"/"开头，从ValidateUtil所在的包下获取资源
		if(in == null){
			return "路径不正确";
		}
		Properties pros = new Properties();
		try {
			pros.load(in);
		} catch (IOException e) {
			return "必须是properties文件";
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		for(Object key : pros.keySet()){
			m.put((String)key, pros.getProperty((String) key));
		}
		return "1";
	}
	
//	public static void main(String[] args) {
//		System.out.println(ValidateUtil.addPattern("regular.properties"));
//		System.out.println(ValidateUtil.addService("validateInterface.properties"));
//	}
}
