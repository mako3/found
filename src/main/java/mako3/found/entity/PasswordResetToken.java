package mako3.found.entity;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PasswordResetToken {

    private String token;

    private String username;

}
