package mako3.found.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mako3.found.dao.SpaceDao;
import mako3.found.entity.ChatSpace;

@Component
public class SpaceService {

    @Autowired
    private SpaceDao spaceDao;

    public List<ChatSpace> findByName(String displayName) {
        return spaceDao.findByName(displayName);
    }

    public List<ChatSpace> findByMember(String memberId) {
        return spaceDao.findByMember(memberId);
    }

    public List<ChatSpace> findAll() {
        return spaceDao.findAll();
    }

    public ChatSpace findOne(String spaceId) {
        return spaceDao.findOne(spaceId);
    }

}