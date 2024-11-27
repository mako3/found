package mako3.found.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mako3.found.dao.MessageDao;
import mako3.found.entity.ChatMessage;

@Component
public class MessageService {

    @Autowired
    private MessageDao messageDao;

    public List<ChatMessage> find(String messageText) {
        return messageDao.find(messageText);
    }

    public List<ChatMessage> list(String spaceId, int limit) {
        return messageDao.list(spaceId, limit);
    }

    public List<ChatMessage> findByUrl(String messageUrl) {
        return messageDao.findByUrl(messageUrl);
    }

}
