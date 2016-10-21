package com.mgear.wuhanparking.common.listener;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

public class ConsumerMessageListener implements MessageListener {

	@Override
	public void onMessage(Message message) {
		if(message instanceof TextMessage) {
			TextMessage tm = (TextMessage) message;
			try {
				String result = tm.getText();
				System.out.println("消费者接收到的消息：" + result);
			} catch (JMSException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("接收到的不是纯文本消息！");
		}

	}

}
