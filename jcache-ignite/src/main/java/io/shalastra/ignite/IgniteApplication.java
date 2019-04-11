package io.shalastra.ignite;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;

import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.configuration.CacheConfiguration;

public class IgniteApplication {

  public static void main(String[] args) {
    System.out.println("Ignite Application...");

    // Get or create a cache manager.
    CacheManager cacheMgr = Caching.getCachingProvider().getCacheManager();

// This is an Ignite configuration object (org.apache.ignite.configuration.CacheConfiguration).
    CacheConfiguration<Integer, String> cfg = new CacheConfiguration<>();

// Specify cache mode and/or any other Ignite-specific configuration properties.
    cfg.setCacheMode(CacheMode.PARTITIONED);

// Create a cache based on the configuration created above.
    Cache<Integer, String> cache = cacheMgr.createCache("aCache", cfg);

// Cache operations
    Integer key = 1;
    String value = "11";
    cache.put(key, "value");

    System.out.println(cache.get(key));
  }
}
