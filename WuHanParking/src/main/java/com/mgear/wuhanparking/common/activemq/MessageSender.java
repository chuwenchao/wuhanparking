package com.mgear.wuhanparking.common.activemq;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

public class MessageSender {
	
	@Autowired
	private JmsTemplate jmsTemplate;
	
	public void sendMessage(final String message){
		System.out.println("--- 生产者产生消息 ---");
		jmsTemplate.send(new MessageCreator(){
			
			@Override
			public Message createMessage(Session session) throws JMSException {
				// TODO Auto-generated method stub
				TextMessage tm = session.createTextMessage("生产者发出的消息--" + message);
				return tm;
			}});
	}
}