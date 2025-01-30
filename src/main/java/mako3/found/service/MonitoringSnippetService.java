package mako3.found.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import mako3.found.dao.KeyValueDao;

@Component
public class MonitoringSnippetService {

    @Autowired
    private KeyValueDao keyValueDao;

    @Cacheable("monitoring.snippet")
    public String getSnippet() {
        return keyValueDao.getValue("monitoring.snippet");
    }

    @CacheEvict(value = "monitoring.snippet", allEntries = true)
    public void updateSnippet(String snippet) {
        keyValueDao.updateValue("monitoring.snippet", snippet);
    }

}
