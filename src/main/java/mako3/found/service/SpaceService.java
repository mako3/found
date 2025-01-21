package mako3.found.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Component;

import mako3.found.auth.CustomUserDetails;
import mako3.found.dao.SpaceDao;
import mako3.found.entity.ChatSpace;
import mako3.found.entity.SpaceQuery;

@Component
public class SpaceService {

    @Autowired
    private SpaceDao spaceDao;

    /** find by space name with accessibility check */
    public List<ChatSpace> findByQuery(CustomUserDetails user, SpaceQuery query) {
        List<String> accessibleSpaceIds = defineSpacesForSearch(user, query);
        return spaceDao.findByName(accessibleSpaceIds, query.getSpaceName());
    }

    private List<String> defineSpacesForSearch(CustomUserDetails user, SpaceQuery query) {
        List<ChatSpace> discoverableSpaces = this.listAllCached()
                .stream()
                .filter(e -> ChatSpace.DISCOVERABLE.equals(e.getAccessState()))
                .toList();
        List<ChatSpace> memberSpaces = user.getMemberSpaces();

        switch (query.getQueryScope()) {
            case PRIVATE_DISCOVERABLE:
                return memberSpaces.stream().map(ChatSpace::getSpaceId).toList();
            case DISCOVERABLE:
                return discoverableSpaces.stream().map(ChatSpace::getSpaceId).toList();
            default:
                throw new IllegalArgumentException("Invalid query scope");
        }
    }

    public List<ChatSpace> findByMember(String memberId) {
        return spaceDao.findByMember(memberId);
    }

    public List<ChatSpace> listAllFresh() {
        return spaceDao.findAll();
    }

    @Cacheable("space-list")
    public List<ChatSpace> listAllCached() {
        return spaceDao.findAll();
    }

    @Cacheable("space-one")
    public ChatSpace getOneCached(String spaceId) {
        return spaceDao.findOne(spaceId);
    }

    public ChatSpace getOne(String spaceId) {
        return spaceDao.findOne(spaceId);
    }

    @Caching(evict = {
            @CacheEvict(value = "space-one", key = "#spaceId"),
            @CacheEvict(value = "space-list", allEntries = true)
    })
    public void updateLastImported(String spaceId, String executorName) {
        spaceDao.updateLastImported(spaceId, executorName);
    }

    @Caching(evict = {
            @CacheEvict(value = "space-one", key = "#spaceId"),
            @CacheEvict(value = "space-list", allEntries = true)
    })
    public void updateMemberIds(String spaceId, List<String> memberIds) {
        spaceDao.updateMemberIds(spaceId, memberIds);
    }

    @Caching(evict = {
            @CacheEvict(value = "space-one", key = "#spaceId"),
            @CacheEvict(value = "space-list", allEntries = true)
    })
    public void updateMessageCount(String spaceId, int messageCount) {
        spaceDao.updateMessageCount(spaceId, messageCount);
    }

}