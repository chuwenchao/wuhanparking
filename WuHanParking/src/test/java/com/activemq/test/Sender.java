package com.activemq.test;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

public class Sender {
	
	private static final int SEND_NUMBER = 5;
	
	public static void main(String[] args) {
		// ConnectionFactory ：连接工厂，JMS 用它创建连接
		ConnectionFactory connectionFactory;
		// Connection ：JMS 客户端到JMS Provider 的连接
		Connection connection = null;
		// Session： 一个发送或接收消息的线程
		Session session = null;
		// Destination ：消息的目的地
		Destination destination;
		// MessageProducer：消息发送者
		MessageProducer producer;
		//实例化连接工厂
		connectionFactory = new ActiveMQConnectionFactory(
				ActiveMQConnection.DEFAULT_USER, 
				ActiveMQConnection.DEFAULT_PASSWORD,
				"tcp://localhost:61616");
		try {
			// 构造从工厂得到连接对象
			connection = connectionFactory.createConnection();
			// 启动
			connection.start();
			// 获取操作连接
			session = connection.createSession(Boolean.TRUE, 
					Session.AUTO_ACKNOWLEDGE);
			//获取消息队列（若不存在则自动创建）
			destination = session.createQueue("TestQueue");
			//得到消息生成者【发送者】
			producer = session.createProducer(destination);
			// 设置持久化
			producer.setDeliveryMode(DeliveryMode.PERSISTENT);
			// 构造消息，此处写死，项目就是参数，或者方法获取
			sendMessage(session, producer);
			session.commit();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			if(session != null) {
				try {
					session.close();
				} catch (JMSException e) {
					e.printStackTrace();
				}
			}
			if(connection != null) {
				try {
					connection.close();
				} catch (JMSException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void sendMessage(Session session, MessageProducer producer) throws JMSException {
		for(int i=1; i<=SEND_NUMBER; i++) {
			TextMessage message = session.createTextMessage("ActiveMq 发送的消息" + i);
			System.out.println("发送消息：" + "ActiveMq 发送的消息" + i);
			// 发送消息
			producer.send(message);
		}
	}
}
