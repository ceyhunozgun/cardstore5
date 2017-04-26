package com.cardstore.sqs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
public class SQSService {
	@Autowired
	protected JmsTemplate defaultJmsTemplate;

	public void sendMessage(String queueName, String messageBody) {
		defaultJmsTemplate.convertAndSend(queueName, messageBody);
	}
}
