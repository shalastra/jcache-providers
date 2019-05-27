package io.shalastra.ignite;

import java.util.concurrent.locks.Lock;

import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;

import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.*;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.transactions.Transaction;

@Slf4j
public class IgniteApplication {

  public static void main(String[] args) {
    try (Ignite ignite = Ignition.start()) {
      CacheConfiguration<String, String> config = new CacheConfiguration<>();
      config.setName("simpleCache");
      config.setTypes(String.class, String.class);
      config.setExpiryPolicyFactory(CreatedExpiryPolicy.factoryOf(Duration.ONE_HOUR));

      IgniteCache<String, String> cache = ignite.getOrCreateCache(config);

      cache.put("key1", "value1");
      cache.put("key2", "value2");

      String value = cache.get("key1");

      log.info(value);

      /**
       * TRANSACTION EXAMPLE
       */
      IgniteTransactions transactions = ignite.transactions();

      try (Transaction tx = transactions.txStart()) {
        String anotherValue = cache.get("key2");

        log.info(anotherValue);

        if (anotherValue == "value2") {
          cache.put("key2", "newValue2");
        }

        cache.put("key3", "value3");

        tx.commit();
      }


      /**
       * LOCK - Cache transactions will acquire locks implicitly. However, there are cases when explicit locks are more useful.
       * The lock() method of the IgniteCache API returns an instance of java.util.concurrent.locks.Lock that lets you define
       * explicit distributed locks for any given key. Locks can also be acquired on a collection of objects using the
       * IgniteCache.lockAll() method.
       */
      Lock lock = cache.lock("key3");
      try {
        lock.lock();

        cache.put("key4", "value4");
        cache.put("key5", "value5");
      }
      finally {
        lock.unlock();
      }

    } catch (IgniteException e) {
      log.error(e.getMessage());
    } finally {
      Ignition.stop(true);
    }
  }
}
