package io.shalastra.hazelcast;


import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;

import com.hazelcast.cache.ICache;
import com.hazelcast.config.CacheConfig;

public class HazelcastApplication {

  public static void main(String[] args) {
    System.out.println("Hazelcast Application...");

    CacheManager manager = Caching.getCachingProvider().getCacheManager();

    ICache<Long, String> cache = manager.createCache("foo", new MutableConfiguration<>()).unwrap(ICache.class);

    CacheConfig cacheConfig = cache.getConfiguration(CacheConfig.class);
  }
}

