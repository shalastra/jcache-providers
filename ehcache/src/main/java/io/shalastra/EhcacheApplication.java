package io.shalastra;

import java.time.Duration;

import bitronix.tm.BitronixTransactionManager;
import bitronix.tm.TransactionManagerServices;
import lombok.extern.slf4j.Slf4j;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.core.Ehcache;
import org.ehcache.transactions.xa.configuration.XAStoreConfiguration;
import org.ehcache.transactions.xa.txmgr.btm.BitronixTransactionManagerLookup;
import org.ehcache.transactions.xa.txmgr.provider.LookupTransactionManagerProviderConfiguration;

@Slf4j
public class EhcacheApplication {

  public static void main(String[] args) {
    try (CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
        .using(new LookupTransactionManagerProviderConfiguration(BitronixTransactionManagerLookup.class)).build()) {
      cacheManager.init();

      CacheConfiguration<String, String> config = CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, String.class,
          ResourcePoolsBuilder.heap(100))
          .add(new XAStoreConfiguration("simpleCache"))
          .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofHours(1)))
          .build();

      Cache<String, String> cache = cacheManager.createCache("simpleCache",
          CacheConfigurationBuilder.newCacheConfigurationBuilder(config));

      cache.put("key1", "value1");
      cache.put("key2", "value2");

      String value = cache.get("key1");

      log.info(value);

      /**
       * TRANSACTION EXAMPLE
       */
      BitronixTransactionManager transactionManager =
          TransactionManagerServices.getTransactionManager();

      transactionManager.begin();
      {
        String anotherValue = cache.get("key2");

        log.info(anotherValue);

        if (anotherValue == "value2") {
          cache.put("key2", "newValue2");
        }

        cache.put("key3", "value3");
      }
      transactionManager.commit();
    }
  }
}
