package mako3.found.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import mako3.found.dao.KeyValueDao;

@Component
public class MailPropertyService {

    @Autowired
    private KeyValueDao keyValueDao;

    @Cacheable("mail-property")
    public MailProperty getMailProperty() {

        String smtpHost = keyValueDao.getValue("mail.host");
        String smtpPort = keyValueDao.getValue("mail.port");
        String smtpUsername = keyValueDao.getValue("mail.username");
        String smtpPassword = keyValueDao.getValue("mail.password");
        String mailSubject = keyValueDao.getValue("mail.subject");
        String mailBodyTemplate = keyValueDao.getValue("mail.body");
        String mailFrom = keyValueDao.getValue("mail.from");

        return MailProperty.builder()
                .smtpHost(smtpHost)
                .stmpPort(smtpPort != null ? Integer.parseInt(smtpPort) : 0)
                .smtpUsername(smtpUsername)
                .smtpPassword(smtpPassword)
                .mailSubject(mailSubject)
                .mailBodyTemplate(mailBodyTemplate)
                .mailFrom(mailFrom)
                .build();

    }

    @CacheEvict("mail-property")
    public void updateMailProperty(MailProperty mailProperty) {
    }

}
