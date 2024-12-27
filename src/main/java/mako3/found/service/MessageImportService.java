package mako3.found.service;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import mako3.found.dao.MessageDao;
import mako3.found.entity.ChatMessage;
import mako3.found.entity.ChatSpace;
import mako3.found.json.GroupMemberJson;
import mako3.found.json.JsonException;
import mako3.found.json.JsonParser;
import mako3.found.json.MessageJson;

@Component
public class MessageImportService {

    private static Log logger = LogFactory.getLog(MessageImportService.class);

    @Autowired
    private MessageDao messageDao;

    @Autowired
    private SpaceService spaceService;

    @Autowired
    private JsonParser parser;

    @Autowired
    private FileSystemStorageService storageService;

    @Transactional
    public void importJson(String spaceId, String filenameOfMessagesJson, String filenamesOfGroupInfoJson,
            String executorName) throws JsonException {
        ChatSpace space = spaceService.getOne(spaceId);
        File fileMessagesJson = storageService.load(filenameOfMessagesJson).toFile();
        File fileGroupInfoJson = storageService.load(filenamesOfGroupInfoJson).toFile();

        importMessages(space, fileMessagesJson);
        importGroupInfo(space, fileGroupInfoJson);

        spaceService.updateLastImported(space.getSpaceId(), executorName);
    }

    private void importMessages(ChatSpace space, File fileMessagesJson) throws JsonException {
        String spaceId = space.getSpaceId();
        List<MessageJson> list = parser.parseMessages(spaceId, fileMessagesJson);
        List<ChatMessage> messageList = list.stream()
                .map(e -> ChatMessage.builder()
                        .spaceId(spaceId)
                        .messageId(e.getMessageId())
                        .messageText(e.getText())
                        .creatorName(e.getCreatorName())
                        .creatorEmail(e.getCreatorEmail())
                        .creatorUserType(e.getCreatorUserType())
                        .createdDate(e.getCreatedDate())
                        .topicId(e.getTopicId())
                        .build())
                .collect(Collectors.toList());
        messageDao.deleteMessagesbySpaceId(spaceId);
        messageDao.insert(messageList);
        messageDao.updateThreadReplyBySpaceId(spaceId);
        messageDao.updateHasReplyBySpaceId(spaceId);
        messageDao.updateTopicCreatedDateBySpaceId(spaceId);
        spaceService.updateMessageCount(spaceId, list.size());
    }

    private void importGroupInfo(ChatSpace space, File fileGroupInfoJson) throws JsonException {
        List<GroupMemberJson> list = parser.parseMembers(space.getDisplayName(), fileGroupInfoJson);
        List<String> memberIds = list.stream().map(e -> e.getEmail()).collect(Collectors.toList());
        spaceService.updateMemberIds(space.getSpaceId(), memberIds);
    }

}
