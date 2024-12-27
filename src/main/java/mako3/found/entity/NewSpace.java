package mako3.found.entity;

import jakarta.validation.constraints.NotBlank;
import lombok.Value;

@Value
public class NewSpace {

    @NotBlank
    private String spaceId;

    @NotBlank
    private String spaceName;

    @NotBlank
    private String accessState;

}
