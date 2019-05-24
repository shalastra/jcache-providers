package io.shalastra.ignite;

import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;

import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteException;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.CacheConfiguration;

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
    } catch (IgniteException e) {
      log.error(e.getMessage());
    } finally {
      Ignition.stop(true);
    }
  }
}
