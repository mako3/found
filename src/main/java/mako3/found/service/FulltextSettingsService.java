package mako3.found.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import mako3.found.dao.KeyValueDao;
import mako3.found.dao.SchemaDao;

@Component
public class FulltextSettingsService {

    private static Log logger = LogFactory.getLog(FulltextSettingsService.class);

    @Autowired
    private KeyValueDao keyValueDao;

    @Autowired
    private SchemaDao schemaDao;

    @Cacheable(cacheNames = "fulltext-settings")
    public boolean isFulltextEnabled() {
        return Boolean.parseBoolean(keyValueDao.getValue("fulltext.index"));
    }

    @CacheEvict(cacheNames = "fulltext-settings", allEntries = true)
    public void updateFulltextEnabled(boolean enabled) {
        if (enabled) {
            long t1 = System.currentTimeMillis();
            schemaDao.createBM25Index();
            long t2 = System.currentTimeMillis();
            logger.info(String.format("succeeded to create BM25 index in %d msec.", t2 - t1));
        } else {
            long t1 = System.currentTimeMillis();
            schemaDao.dropBM25Index();
            long t2 = System.currentTimeMillis();
            logger.info(String.format("succeeded to drop BM25 index in %d msec.", t2 - t1));
        }
        keyValueDao.updateValue("fulltext.index", enabled ? "true" : "false");
    }

}
