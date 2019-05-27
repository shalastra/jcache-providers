package io.shalastra.jcache;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.AccessedExpiryPolicy;
import javax.cache.spi.CachingProvider;

import lombok.extern.slf4j.Slf4j;

import static javax.cache.expiry.Duration.ONE_HOUR;

@Slf4j
public class JcacheApplication {

  public static void main(String[] args) {
    CachingProvider cachingProvider = Caching.getCachingProvider();

    CacheManager cacheManager = cachingProvider.getCacheManager();

    MutableConfiguration<String, String> config
        = new MutableConfiguration<>();

    config.setStoreByValue(false)
        .setTypes(String.class, String.class)
        .setExpiryPolicyFactory(AccessedExpiryPolicy.factoryOf(ONE_HOUR));

    try (Cache<String, String> cache = cacheManager.createCache("simpleCache", config)) {
      cache.put("key1", "value1");
      cache.put("key2", "value2");

      String value = cache.get("key1");

      log.info(value);


      /**
       * ENTRY PROCESSOR
       */
      String newValue = "newValue1";
      cache.invoke("key2", (entry, arguments) -> {
        String anotherValue = entry.getValue();
        log.info(anotherValue);

        if (anotherValue == "value2") {
          entry.setValue(newValue);
        }

        return null;
      });

    } finally {
      cacheManager.close();
    }
  }
}
