package mako3.found.mail;

import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class MailSenderService {

    private MailSender mailSender;

    @Async
    public void sendMail(String to, String subject, String body) {
        var mailInfo = new SimpleMailMessage();
        mailInfo.setSubject("");
        mailInfo.setText("");
        mailInfo.setTo("");
        mailInfo.setFrom("");

        mailSender.send(mailInfo);
    }

}
