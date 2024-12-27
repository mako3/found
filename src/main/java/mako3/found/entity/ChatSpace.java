package mako3.found.entity;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatSpace {

    public static final String DISCOVERABLE = "DISCOVERABLE";
    public static final String PRIVATE = "PRIVATE";

    private String spaceId;

    private String displayName;

    // PRIVATE or DISCOVERABLE
    private String accessState;

    private String lastImportedUser;

    private LocalDateTime lastImportedDate;

    private List<String> memberIds;

    private int memberCount;

    private int messageCount;

    public boolean getImported() {
        return lastImportedUser != null && !lastImportedUser.isEmpty();
    }

}
