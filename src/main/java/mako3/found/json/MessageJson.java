package mako3.found.json;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;

import lombok.Getter;
import lombok.ToString;

@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
public class MessageJson {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy年M月d日EEEE H:m:s 'UTC'",
            Locale.JAPANESE);

    private String text;
    private String topicId;
    private String messageId;
    private LocalDateTime createdDate;
    private String creatorName;
    private String creatorEmail;
    private String creatorUserType;

    @JsonSetter("created_date")
    public void setCreatedDate(String createdDate) {
        try {
            this.createdDate = LocalDateTime.parse(createdDate, FORMATTER);
        } catch (DateTimeParseException e) {
            e.printStackTrace();
            this.createdDate = null;
        }
    }

    @JsonSetter("text")
    public void setText(String text) {
        this.text = text;
    }

    @JsonSetter("topic_id")
    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    @JsonSetter("message_id")
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    @JsonSetter("creator")
    public void setCreator(Map<String, Object> creator) {
        this.creatorName = (String) creator.get("name");
        this.creatorEmail = (String) creator.get("email");
        this.creatorUserType = (String) creator.get("user_type");
    }

}
