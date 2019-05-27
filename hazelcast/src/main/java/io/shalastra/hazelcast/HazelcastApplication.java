package io.shalastra.hazelcast;

import java.util.Map;
import java.util.concurrent.locks.Lock;

import com.hazelcast.config.CacheSimpleConfig;
import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.TransactionalMap;
import com.hazelcast.transaction.TransactionContext;
import com.hazelcast.transaction.TransactionOptions;
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
    IMap<String, String> cache = hazelcastInstance.getMap("simpleCache");

    cache.put("key1", "value1");
    cache.put("key2", "value2");

    String value = cache.get("key1");

    log.info(value);


    /**
     * TRANSACTION - In a transaction, operations are not executed immediately. Their changes are local to the TransactionContext
     * until committed. However, they ensure the changes via locks. For the above example, when map.put is executed, no data is put
     * in the map but the key is locked against changes. While committing, operations are executed, the value is put to the map and
     * the key is unlocked.
     */
    TransactionOptions options = new TransactionOptions().setTransactionType(TransactionOptions.TransactionType.ONE_PHASE);
    TransactionContext context = hazelcastInstance.newTransactionContext();
    context.beginTransaction();

    TransactionalMap map = context.getMap("simpleCache");

    try {
      String anotherValue = cache.get("key2");

      log.info(anotherValue);

      if (anotherValue == "value2") {
        cache.put("key2", "newValue2");
      }

      cache.put("key3", "value3");

      context.commitTransaction();
    }catch (Throwable t)  {
      context.rollbackTransaction();
    }

    /**
     * lOCK - To perform pessimistic locking, use the lock mechanism provided by the Hazelcast distributed map, i.e., the map.lock and
     * map.unlock methods. See the below example code. The IMap lock will automatically be collected by the garbage collector when the
     * lock is released and no other waiting conditions exist on the lock.
     *
     * The IMap lock is reentrant, but it does not support fairness.
     */
    cache.lock("key3");
    try {
      cache.put("key4", "value4");
      cache.put("key5", "value5");
    }
    finally {
      cache.unlock("key3");
    }

    hazelcastInstance.shutdown();
  }
}

