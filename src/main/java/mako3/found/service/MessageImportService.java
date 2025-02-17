package mako3.found.service;

import java.io.File;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import mako3.found.dao.MessageDao;
import mako3.found.entity.ChatMessage;
import mako3.found.entity.ChatSpace;
import mako3.found.entity.Task;
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

    @Autowired
    private TaskService importTaskService;

    @Async("import-thread")
    public CompletableFuture<Void> importJsonAync(String spaceId, String filenameOfMessagesJson,
            String filenamesOfGroupInfoJson,
            String executorUsername, String taskId) {

        try {
            long t1 = System.currentTimeMillis();
            importTaskService.updateInProgress(taskId);
            spaceService.updateImportStatus(spaceId, Task.Status.IN_PROGRESS.getValue());
            ChatSpace space = spaceService.getOne(spaceId);
            File fileMessagesJson = storageService.load(filenameOfMessagesJson).toFile();
            File fileGroupInfoJson = storageService.load(filenamesOfGroupInfoJson).toFile();

            // should be transactional 
            importJson(space, fileMessagesJson, fileGroupInfoJson, executorUsername);

            long t2 = System.currentTimeMillis();
            logger.info(String.format("succeeded to import json for space %s in %d msec.", spaceId, t2 - t1));
            importTaskService.updateSuccess(taskId);
        } catch (JsonException e) {
            logger.error(String.format("failed to import json for space %s", spaceId), e);
            importTaskService.updateFailure(taskId, e.getMessage());
            spaceService.updateImportStatus(spaceId, Task.Status.FAILED.getValue());
        } catch (Exception e) {
            logger.error(String.format("failed to import json for space %s", spaceId), e);
            importTaskService.updateFailure(taskId, "unexpected internal error");
            spaceService.updateImportStatus(spaceId, Task.Status.FAILED.getValue());
        }

        return CompletableFuture.completedFuture(null);
    }

    @Transactional
    private void importJson(ChatSpace space, File fileMessagesJson, File fileGroupInfoJson, String executorUsername)
            throws JsonException {

        importMessages(space, fileMessagesJson);
        importGroupInfo(space, fileGroupInfoJson);
        spaceService.updateLastImported(space.getSpaceId(), executorUsername);
        spaceService.updateImportStatus(space.getSpaceId(), Task.Status.SUCCEEDED.getValue());
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
                        .attachedFiles(e.getAttachedFiles() != null ? e.getAttachedFiles() : List.of())
                        .build())
                .collect(Collectors.toList());
        messageDao.deleteMessagesbySpaceId(spaceId);
        messageDao.insert(messageList);
        messageDao.updateThreadReplyBySpaceId(spaceId);
        messageDao.updateHasReplyBySpaceId(spaceId);
        messageDao.updateTopicCreatedDateBySpaceId(spaceId);
        messageDao.updateDisplaySeq(spaceId);
        spaceService.updateMessageCount(spaceId, list.size());
        logger.info(String.format("succeeded to import %d messages for space %s", list.size(), spaceId));
    }

    private void importGroupInfo(ChatSpace space, File fileGroupInfoJson) throws JsonException {
        List<GroupMemberJson> list = parser.parseMembers(space.getDisplayName(), fileGroupInfoJson);
        List<String> memberIds = list.stream().map(e -> e.getEmail()).collect(Collectors.toList());
        spaceService.updateMemberIds(space.getSpaceId(), memberIds);
        logger.info(String.format("succeeded to import space members for space %s", space.getSpaceId()));
    }

}
