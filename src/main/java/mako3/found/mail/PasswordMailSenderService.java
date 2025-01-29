package mako3.found.mail;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templateresolver.StringTemplateResolver;

import jakarta.annotation.PostConstruct;
import jakarta.mail.internet.MimeMessage;

@Component
public class PasswordMailSenderService {

    private static Log logger = LogFactory.getLog(PasswordMailSenderService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private MailPropertyService mailPropertyService;

    @PostConstruct
    public void init() {
        MailProperty prop = mailPropertyService.getMailProperty();

        // override configuration on application.yaml by database 
        JavaMailSenderImpl sender = (JavaMailSenderImpl) this.mailSender;
        sender.setHost(prop.getSmtpHost());
        sender.setPort(prop.getSmtpPort());
        sender.setUsername(prop.getSmtpUsername());
        sender.setPassword(prop.getSmtpPassword());

        logger.info(String.format("smtp host has been overrided into %s", prop.getSmtpHost()));
    }

    @Retryable(retryFor = {
            MailSendingException.class }, maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 2), recover = "sendMailRecover")
    public void sendPasswordMail(String to, String username, String password) throws MailSendingException {
        MailProperty prop = mailPropertyService.getMailProperty();

        final SpringTemplateEngine engine = new SpringTemplateEngine();
        engine.setTemplateResolver(new StringTemplateResolver());

        // construct mail body
        final Map<String, Object> variables = new HashMap<>();
        variables.put("username", username);
        variables.put("password", password);
        final Context context = new Context();
        context.setVariables(variables);
        final String textBody = engine.process(prop.getMailBodyTemplate1(), context);

        // actually send a mail
        doSend(prop.getMailFrom(), to, prop.getMailSubject1(), textBody);
    }

    @Retryable(retryFor = {
            MailSendingException.class }, maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 2))
    public void sendResetTokenMail(String to, String token) {
        MailProperty prop = mailPropertyService.getMailProperty();

        final SpringTemplateEngine engine = new SpringTemplateEngine();
        engine.setTemplateResolver(new StringTemplateResolver());

        // construct mail body
        final Map<String, Object> variables = new HashMap<>();
        variables.put("token", token);
        final Context context = new Context();
        context.setVariables(variables);
        final String textBody = engine.process(prop.getMailBodyTemplate2(), context);

        // actually send a mail
        doSend(prop.getMailFrom(), to, prop.getMailSubject2(), textBody);
    }

    private void doSend(String from, String to, String subject, String textBody) throws MailSendingException {
        try {
            // actually send a mail
            final MimeMessage mimeMessage = mailSender.createMimeMessage();
            final MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, StandardCharsets.UTF_8.name());
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(textBody);

            this.mailSender.send(mimeMessage);
            logger.info(String.format("succeeded to send a mail to %s", to));
        } catch (Exception e) {
            throw new MailSendingException(String.format("failed to send a mail to %s", to), e);
        }
    }

}
