package com.mgear.wuhanparking.common.listener;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CloseListener implements ServletContextListener {
	static final Logger log = LoggerFactory.getLogger(CloseListener.class);
	
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		//手动关闭线程池
//		ThreadPool.getInstance().shutdown();
		
		//手动注销JDBC driver
		Enumeration<Driver> drivers = DriverManager.getDrivers();
		Driver d = null;
		while(drivers.hasMoreElements()) {
			try {
				d = drivers.nextElement();
				DriverManager.deregisterDriver(d);
				log.info("Driver is deregister : " + d);
			} catch (SQLException e) {
				log.error(String.format("Error deregister driver", d), e);
			}
		}
		
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
