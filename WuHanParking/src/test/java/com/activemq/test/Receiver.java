package com.activemq.test;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

public class Receiver {

	public static void main(String[] args) {
		ConnectionFactory connectionFactory;
		Connection connection = null;
		Session session = null;
		Destination destination;
		MessageConsumer consumer;
		connectionFactory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_USER,
				ActiveMQConnection.DEFAULT_PASSWORD, 
				"tcp://localhost:61616");
		try {
			connection = connectionFactory.createConnection();
			connection.start();
			session = connection.createSession(Boolean.FALSE, Session.AUTO_ACKNOWLEDGE);
			destination = session.createQueue("TestQueue");
			consumer = session.createConsumer(destination);
			while(true) {
				TextMessage message = (TextMessage) consumer.receive(100000);
				if(message != null) {
					System.out.println("收到消息" + message.getText());
				} else {
					break;
				}
			}
		} catch (Exception e) {
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

}
