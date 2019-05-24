package io.shalastra.hazelcast;

import java.util.Map;

import com.hazelcast.config.CacheSimpleConfig;
import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HazelcastApplication {

  public static void main(String[] args) {
    Config config = new Config();

    CacheSimpleConfig cacheConfig = new CacheSimpleConfig();
    cacheConfig.setName("simpleCache");
    cacheConfig.setKeyType("String");
    cacheConfig.setValueType("String");

    config.addCacheConfig(cacheConfig);

    HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance(config);

    // Other possibilities are: Map, List, Set, Queues
    Map<String, String> cache = hazelcastInstance.getMap("simpleCache");

    cache.put("key1", "value1");
    cache.put("key2", "value2");

    String value = cache.get("key1");

    log.info(value);

    hazelcastInstance.shutdown();
  }
}

