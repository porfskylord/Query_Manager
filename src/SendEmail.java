import java.util.Properties;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class SendEmail {

    public static void sendNotification() {
        String recipient = "azadmuk011098@gmail.com";
        String sender = "porfskylord@gmail.com";
        String appPassword = "wygu nlzt wmln poah";

        String user = System.getProperty("user.name");
        String computerName = "Unknown";
        String ipAddress = "Unknown";

        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            computerName = inetAddress.getHostName();
            ipAddress = inetAddress.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        String messageBody = "Your JavaFX app was started!\n\n" +
                "👤 User: " + user + "\n" +
                "💻 Computer: " + computerName + "\n" +
                "🌐 IP Address: " + ipAddress + "\n" +
                "📅 Time: " + java.time.LocalDateTime.now();


        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");


        Session session = Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(sender, appPassword);
            }
        });

        try {

            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(sender));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
            message.setSubject("Someone Is Running Your Query Manager " + computerName);


            message.setText(messageBody);


            Transport.send(message);
            System.out.println("✅ Mail successfully sent!");
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }
}
