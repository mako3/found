package mako3.found.entity;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class MessageQuery {

    public enum QueryType {
        url,
        messageText
    }

    public enum QueryScope {
        PRIVATE_DISCOVERABLE,
        DISCOVERABLE,
        SPACE
    }

    private QueryType queryType;

    private String keyword;

    private String spaceId;

    private LocalDate startDate;

    private LocalDate endDate;

    private QueryScope queryScope;

    private String creatorEmail;

    private int limit;

}
