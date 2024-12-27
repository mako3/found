package mako3.found.entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatMessage {

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

    private String spaceId;

    private String creatorName;

    private String creatorEmail;

    private String creatorUserType;

    private LocalDateTime createdDate;

    private String messageText;

    private String topicId;

    private String messageId;

    private boolean threadReply;

    private boolean hasReply;

    public String getUrlSafeCreatedDate() {
        return createdDate.format(FORMATTER);
    }

}