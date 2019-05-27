package io.shalastra;

import java.time.Duration;

import lombok.extern.slf4j.Slf4j;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;

@Slf4j
public class EhcacheApplication {

  public static void main(String[] args) {
    try (CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder().build()) {
      cacheManager.init();

      CacheConfiguration<String, String> config = CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, String.class,
          ResourcePoolsBuilder.heap(100))
          .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofHours(1)))
          .build();

      Cache<String, String> cache = cacheManager.createCache("simpleCache",
          CacheConfigurationBuilder.newCacheConfigurationBuilder(config));

      cache.put("key1", "value1");
      cache.put("key2", "value2");

      String value = cache.get("key1");

      log.info(value);
  }
}
