package businessLogic;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Servicio de envío de emails usando SMTP sin autenticación.
 */
public class EmailService {

	private static final String SMTP_HOST = "smtp.ehu.es";
	private static final String SENDER_EMAIL = "market@market.com";

	public static void sendEmail(String recipientEmail, String subject, String body) throws Exception {
		Properties props = new Properties();
		props.put("mail.smtp.host", SMTP_HOST);
		
		Session session = Session.getInstance(props);
		
		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress(SENDER_EMAIL));
		message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
		message.setSubject(subject);
		message.setText(body);
		
		Transport.send(message);
		System.out.println("✅ Email enviado a: " + recipientEmail);
	}
}
