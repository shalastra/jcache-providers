package io.shalastra.infinispan;


import java.util.concurrent.TimeUnit;

import javax.transaction.*;

import lombok.extern.slf4j.Slf4j;
import org.infinispan.Cache;
import org.infinispan.commons.CacheException;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.context.Flag;
import org.infinispan.manager.DefaultCacheManager;

@Slf4j
public class InfinispanApplication {

  public static void main(String[] args) throws SystemException {
    DefaultCacheManager cacheManager = new DefaultCacheManager();
    ConfigurationBuilder config = new ConfigurationBuilder();
    config.expiration().lifespan(1, TimeUnit.HOURS);

    Cache<String, String> cache = cacheManager.createCache("simpleCache", config.build());
    cache.put("key1", "value1");
    cache.put("key2", "value2");

    String value = cache.get("key1");

    log.info(value);

    TransactionManager transactionManager = cache.getAdvancedCache().getTransactionManager();

    try {
      transactionManager.begin();

      String anotherValue = cache.get("key2");

      log.info(anotherValue);

      if (anotherValue == "value2") {
        cache.put("key2", "newValue2");
      }

      cache.put("key3", "value3");

      transactionManager.commit();
    } catch (Exception e) {
      transactionManager.rollback();
      log.error(e.getMessage());
    }

    /**
     * LOCK - supposed be located inside of the transaction. It serves in pessimistic scenarios
     */
    try {
      cache.getAdvancedCache().withFlags(Flag.FORCE_WRITE_LOCK).get("key3");
        cache.put("key4", "value4");
        cache.put("key5", "value5");
    } catch (CacheException e) {

    }

    cacheManager.stop();
  }
}
