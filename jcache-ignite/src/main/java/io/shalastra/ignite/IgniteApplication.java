package io.shalastra.ignite;

import javax.cache.CacheManager;
import javax.cache.Caching;

import org.apache.ignite.IgniteCache;
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
    IgniteCache<String, Integer> cache = cacheMgr.createCache("aCache", cfg).unwrap(IgniteCache.class);

    // Put-if-absent which returns previous value.
    Integer oldVal = cache.getAndPutIfAbsent("Hello", 11);

    // Put-if-absent which returns boolean success flag.
    boolean success = cache.putIfAbsent("World", 22);

    // Replace-if-exists operation (opposite of getAndPutIfAbsent), returns previous value.
    oldVal = cache.getAndReplace("Hello", 11);

    // Replace-if-exists operation (opposite of putIfAbsent), returns boolean success flag.
    success = cache.replace("World", 22);

    // Replace-if-matches operation.
    success = cache.replace("World", 2, 22);

    // Remove-if-matches operation.
    success = cache.remove("Hello", 1);
  }
}
