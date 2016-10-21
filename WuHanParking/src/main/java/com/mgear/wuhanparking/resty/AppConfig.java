package com.mgear.wuhanparking.resty;

import cn.dreampie.orm.ActiveRecordPlugin;
import cn.dreampie.orm.provider.druid.DruidDataSourceProvider;
import cn.dreampie.route.config.Config;
import cn.dreampie.route.config.HandlerLoader;
import cn.dreampie.route.config.PluginLoader;
import cn.dreampie.route.config.ResourceLoader;
import cn.dreampie.route.handler.cors.CORSHandler;

public class AppConfig extends Config {

	  public void configResource(ResourceLoader resourceLoader) {
		   //设置resource的目录  减少启动扫描目录
		   resourceLoader.addIncludePackages("com.mgear.wuhanparking.resty");
	  }

	  public void configPlugin(PluginLoader pluginLoader) {
	    //第一个数据库
//		    DruidDataSourceProvider ddsp = new DruidDataSourceProvider("default");
//		    ActiveRecordPlugin activeRecordPlugin = new ActiveRecordPlugin(ddsp, true);
//		    activeRecordPlugin.addIncludePaths("cn.dreampie.resource");
//		    pluginLoader.add(activeRecordPlugin);
		  
		//第二个数据源 使用druid连接池 数据源名字 demo
		    DruidDataSourceProvider ddsp = new DruidDataSourceProvider();
		    ActiveRecordPlugin activeRecordDdsp = new ActiveRecordPlugin(ddsp);
		    pluginLoader.add(activeRecordDdsp);
	  }

	  public void configHandler(HandlerLoader handlerLoader) {
	    //跨域
	    handlerLoader.add(new CORSHandler());
	  }
}
