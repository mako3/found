package mako3.found.json;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
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
    private List<String> attachedFiles;

    @JsonSetter("attached_files")
    public void setAttachedFiles(List<Map<String, Object>> attachedFiles) {
        // cut the file name to 200 characters
        this.attachedFiles = attachedFiles.stream().map(m -> {
            String rawFilename = (String) m.get("export_name");
            return rawFilename.length() > 200 ? rawFilename.substring(0, 200) : rawFilename;
        }).toList();
    }

    @JsonSetter("created_date")
    public void setCreatedDate(String utcCreatedDate) {
        try {
            LocalDateTime utc = LocalDateTime.parse(utcCreatedDate, FORMATTER);
            ZonedDateTime jst = ZonedDateTime.of(utc, ZoneId.of("UTC")).withZoneSameInstant(ZoneId.of("Asia/Tokyo"));
            this.createdDate = jst.toLocalDateTime();
        } catch (DateTimeParseException e) {
            e.printStackTrace();
            this.createdDate = null;
        }
    }

    @JsonSetter("deleted_date")
    public void setDeletedDate(String utcDeletedDate) {
        try {
            LocalDateTime utc = LocalDateTime.parse(utcDeletedDate, FORMATTER);
            ZonedDateTime jst = ZonedDateTime.of(utc, ZoneId.of("UTC")).withZoneSameInstant(ZoneId.of("Asia/Tokyo"));
            this.createdDate = jst.toLocalDateTime();
        } catch (DateTimeParseException e) {
            e.printStackTrace();
            this.createdDate = null;
        }
    }

    @JsonSetter("message_state")
    public void setMessageState(String messageState) {
        if ("DELETED".equals(messageState)) {
            this.text = "DELETED";
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
