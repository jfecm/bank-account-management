package com.jfecm.bankaccountmanagement.service;

import javax.mail.MessagingException;

// JavaMail API
public interface EmailService {
	void sendEmail(String receiver, String subject, String message) throws MessagingException;
}
