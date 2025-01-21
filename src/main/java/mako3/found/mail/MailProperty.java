package mako3.found.mail;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class MailProperty {

    private String smtpHost;

    private int smtpPort;

    private String smtpUsername;

    private String smtpPassword;

    private String mailSubject;

    private String mailBodyTemplate;

    private String mailFrom;

}
