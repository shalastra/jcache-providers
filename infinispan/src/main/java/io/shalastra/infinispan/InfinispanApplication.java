package io.shalastra.infinispan;


import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;
import org.infinispan.Cache;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;

@Slf4j
public class InfinispanApplication {

  public static void main(String[] args) {
    DefaultCacheManager cacheManager = new DefaultCacheManager();
    ConfigurationBuilder config = new ConfigurationBuilder();
    config.expiration().lifespan(1, TimeUnit.HOURS);

    Cache<String, String> cache = cacheManager.createCache("simpleCache", config.build());
    cache.put("key1", "value1");
    cache.put("key2", "value2");

    String value = cache.get("key1");

    log.info(value);

    cacheManager.stop();
  }
}
