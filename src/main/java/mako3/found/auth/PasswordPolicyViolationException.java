package mako3.found.auth;

public class PasswordPolicyViolationException extends Exception {

    private static final long serialVersionUID = 1L;

    public PasswordPolicyViolationException(String message) {
        super(message);
    }

    public PasswordPolicyViolationException(String message, Throwable cause) {
        super(message, cause);
    }
}
