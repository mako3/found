package mako3.found.entity;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatSpace {

    private String spaceId;

    private String displayName;

    private String accessState;

    private String lastImportedUser;

    private LocalDateTime lastImportedDate;

    private List<String> memberIds;

    private int memberCount;

    private int messageCount;

}
