package mako3.found.mail;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class MailProperty {

    @NotBlank
    private String smtpHost;

    private int smtpPort;

    private String smtpUsername;

    private String smtpPassword;

    /** subject for password mail */
    private String mailSubject1;

    /** body for password mail */
    private String mailBodyTemplate1;

    /** subject for reset token mail */
    private String mailSubject2;

    /** body for reset token mail */
    private String mailBodyTemplate2;

    @Email
    private String mailFrom;

}
