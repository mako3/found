package mako3.found.mail;

public class MailSendingException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public MailSendingException(String message, Throwable cause) {
        super(message, cause);
    }

    public MailSendingException(String message) {
        super(message);
    }
}
