package mako3.found.service;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import mako3.found.dao.MessageDao;
import mako3.found.dao.SpaceDao;
import mako3.found.entity.ChatMessage;
import mako3.found.entity.ChatSpace;
import mako3.found.json.GroupMemberJson;
import mako3.found.json.JsonException;
import mako3.found.json.JsonParser;
import mako3.found.json.MessageJson;

@Component
public class MessageImportService {

    @Autowired
    private MessageDao messageDao;

    @Autowired
    private SpaceDao spaceDao;

    @Autowired
    private JsonParser parser;

    @Autowired
    private FileSystemStorageService storageService;

    public void recordTimestamp(String spaceId, String timestampOfMessagesJson) {

    }

    @Transactional
    public void importJson(String spaceId, String filenameOfMessagesJson, String filenamesOfGroupInfoJson,
            String executorName) {
        ChatSpace space = spaceDao.findOne(spaceId);
        File fileMessagesJson = storageService.load(filenameOfMessagesJson).toFile();
        File fileGroupInfoJson = storageService.load(filenamesOfGroupInfoJson).toFile();

        try {
            importMessages(space, fileMessagesJson);
            importGroupInfo(space, fileGroupInfoJson);

            spaceDao.updateLastImported(space.getSpaceId(), executorName);
        } catch (JsonException e) {
            e.printStackTrace();
        }
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
        spaceDao.updateMessageCount(spaceId, list.size());
    }

    private void importGroupInfo(ChatSpace space, File fileGroupInfoJson) throws JsonException {
        List<GroupMemberJson> list = parser.parseMembers(space.getDisplayName(), fileGroupInfoJson);
        List<String> memberIds = list.stream().map(e -> e.getEmail()).collect(Collectors.toList());
        spaceDao.updateMemberIds(space.getSpaceId(), memberIds);
    }

}
