package com.cardstore.ses;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;

@Service
public class SESService {
	@Autowired
	SESConfig sesConfig;

	public void sendMessage(String to, String subject, String body) {
		Destination destination = new Destination().withToAddresses(to);
		Content subj = new Content().withData(subject);
		Content bdy = new Content().withData(body);

		Message message = new Message().withSubject(subj).withBody(new Body().withHtml(bdy));
		SendEmailRequest request = new SendEmailRequest().withSource(sesConfig.getFromAddress())
				.withDestination(destination).withMessage(message);
		sesConfig.amazonSimpleEmailService().sendEmail(request);
	}
}
