package com.dharmeshborad.rallyreminder.core;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailSender {
	public final int MESSAGE_CHUNKS = 3;
	public final int FORM_CHUNKS = 9;
	public final int CHUNK_LEN = 36;
	protected String MAIL_MSG;

	public void sendMail(String subject, String message, String receivers) {
		final Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.server.com"); //Put your smtp server provider
		props.put("mail.from", "no-reply@no.reply");
		final Session session = Session.getInstance(props, null);

		try {
			final MimeMessage msg = new MimeMessage(session);
			msg.setFrom();
			msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(receivers));
			final String strMailSubject = subject;
			msg.setSubject(strMailSubject);
			final Date currentDate = new Date();
			final DateFormat dateTimeFormat = new SimpleDateFormat("yyyy-dd-MM hh:mm:ss");
			final String currentDateTime = dateTimeFormat.format(currentDate);
			msg.setSentDate(currentDate);
			final String strMailMsg = message;
			msg.setText(strMailMsg,"UTF-8","html");
			Transport.send(msg);
		} catch (final MessagingException mex) { 
			System.out.println("send failed, exception: " + mex);
		}
	}

}