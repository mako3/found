package mako3.found.entity;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class SpaceQuery {

    public enum QueryScope {
        PRIVATE_DISCOVERABLE,
        DISCOVERABLE
    }

    private QueryScope queryScope;

    private String spaceName;

}
