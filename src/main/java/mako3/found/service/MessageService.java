package mako3.found.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mako3.found.dao.MessageDao;
import mako3.found.entity.ChatMessage;

@Component
public class MessageService {

    @Autowired
    private MessageDao messageDao;

    public List<ChatMessage> find(String rawText, int limit) {
        List<String> sanitizedTermList = List.of(rawText.replaceAll("　", " ").split(" "));
        return messageDao.findByTerms(sanitizedTermList, limit);
    }

    public List<ChatMessage> list(String spaceId, int limit) {
        return messageDao.list(spaceId, limit);
    }

    public List<ChatMessage> listFrom(String spaceId, LocalDateTime dateFrom, int limit) {
        return messageDao.listFrom(spaceId, dateFrom, limit);
    }

    public List<ChatMessage> listBefore(String spaceId, LocalDateTime dateBefore, int limit) {
        return messageDao.listBefore(spaceId, dateBefore, limit);
    }

    public List<ChatMessage> findByUrl(String messageUrl) {
        return messageDao.findByUrl(messageUrl);
    }

}
