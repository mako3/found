package mako3.found.entity;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Value;

@Value
public class NewUser {

    @NotBlank
    private String username;

    @NotBlank
    private String role;

    @Email
    private String emailForNotification;

    @Email
    private String emailForMessageIdentity;
}
