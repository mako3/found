package mako3.found.service;

import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import mako3.found.auth.CustomUserDetails;
import mako3.found.dao.MessageDao;
import mako3.found.entity.ChatMessage;
import mako3.found.entity.ChatSpace;
import mako3.found.entity.MessageQuery;

@Component
public class MessageService {

    private static Log logger = LogFactory.getLog(MessageImportService.class);

    @Autowired
    private MessageDao messageDao;

    @Autowired
    private SpaceService spaceService;

    /** find by query with accessibility check */
    public List<ChatMessage> find(CustomUserDetails user, MessageQuery query) {
        switch (query.getQueryType()) {
            case url:
                return injectSpaceNames(findByUrl(user, query.getKeyword()));
            case messageText:
                return injectSpaceNames(findByTerms(user, query));
        }
        return null;
    }

    private List<ChatMessage> injectSpaceNames(List<ChatMessage> src) {
        List<ChatSpace> spaces = spaceService.listAllCached();
        for (ChatMessage message : src) {
            String spaceName = spaces.stream()
                    .filter(e -> e.getSpaceId().equals(message.getSpaceId())).findAny().get()
                    .getDisplayName();
            message.setSpaceName(spaceName);
        }
        return src;
    }

    /** find by url with accessibility check */
    private List<ChatMessage> findByUrl(CustomUserDetails user, String url) {
        List<ChatMessage> messages = messageDao.findByUrl(url);

        List<ChatSpace> discoverableSpaces = spaceService.listAllCached()
                .stream()
                .filter(e -> ChatSpace.DISCOVERABLE.equals(e.getAccessState()))
                .toList();
        List<ChatSpace> memberSpaces = user.getMemberSpaces();

        // check whether the message can be accessible by the user
        List<ChatMessage> filteredMessages = messages
                .stream()
                .filter(e -> discoverableSpaces.stream().anyMatch(s -> s.getSpaceId().equals(e.getSpaceId()))
                        || memberSpaces.stream().anyMatch(s -> s.getSpaceId().equals(e.getSpaceId())))
                .toList();

        return filteredMessages;
    }

    private String sanitize(String raw) {
        // sanitized special characters defined at https://docs.paradedb.com/documentation/full-text/overview
        Set<Character> specialChars = Set.of(
                '+', '^', '`', ':', '{', '}', '"', '[', ']', '(', ')', '<', '>', '~', '!', '*', '\\');
        StringBuilder sb = new StringBuilder();
        for (char c : raw.toCharArray()) {
            if (specialChars.contains(c)) {
                sb.append('\\');
            }
            sb.append(c);
        }
        return sb.toString();
    }

    private List<ChatMessage> findByTerms(CustomUserDetails user, MessageQuery query) {
        String[] spaceSplittedTermArray = query.getKeyword().replaceAll("　", " ").split("\s");
        List<String> spaceSplittedTermList = spaceSplittedTermArray.length == 1 && spaceSplittedTermArray[0].isEmpty()
                ? List.of()
                : List.of(spaceSplittedTermArray);
        List<String> sanitizedTermList = spaceSplittedTermList.stream().map(this::sanitize).toList();
        List<String> accessibleSpaceIds = defineSpacesForSearch(user, query);

        return messageDao.findByTerms(accessibleSpaceIds, sanitizedTermList, query.getStartDate(),
                query.getEndDate(),
                query.getCreatorEmail(), query.getLimit());
    }

    private List<String> defineSpacesForSearch(CustomUserDetails user, MessageQuery query) {
        List<ChatSpace> discoverableSpaces = spaceService.listAllCached()
                .stream()
                .filter(e -> ChatSpace.DISCOVERABLE.equals(e.getAccessState()))
                .toList();
        List<ChatSpace> memberSpaces = user.getMemberSpaces();

        switch (query.getQueryScope()) {
            case PRIVATE_DISCOVERABLE:
                return memberSpaces.stream().map(ChatSpace::getSpaceId).toList();
            case DISCOVERABLE:
                return discoverableSpaces.stream().map(ChatSpace::getSpaceId).toList();
            case SPACE:
                return List.of(query.getSpaceId());
            default:
                throw new IllegalArgumentException("Invalid query scope");
        }
    }

    /** get message list with accessibility check */
    public List<ChatMessage> list(CustomUserDetails user, String spaceId, int seqFrom, int limit)
            throws AccessDeniedException {
        if (!isAccessibleSpace(user, spaceId)) {
            throw new AccessDeniedException("You are not allowed to access this space.");
        }
        return messageDao.list(spaceId, seqFrom, limit);
    }

    private boolean isAccessibleSpace(CustomUserDetails user, String spaceId) {
        List<ChatSpace> discoverableSpaces = spaceService.listAllCached()
                .stream()
                .filter(e -> ChatSpace.DISCOVERABLE.equals(e.getAccessState()))
                .toList();
        List<ChatSpace> memberSpaces = user.getMemberSpaces();

        return discoverableSpaces.stream().anyMatch(e -> e.getSpaceId().equals(spaceId))
                || memberSpaces.stream().anyMatch(e -> e.getSpaceId().equals(spaceId));
    }

}
