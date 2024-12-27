package mako3.found.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import mako3.found.dao.KeyValueDao;

@Component
public class NoticeService {

    @Autowired
    private KeyValueDao keyValueDao;

    @Cacheable("notice")
    public String getNotice() {
        return keyValueDao.getValue("notice");
    }

    @CacheEvict(value = "notice", allEntries = true)
    public void updateNotice(String notice) {
        keyValueDao.updateValue("notice", notice);
    }

}
