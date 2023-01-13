package com.colak.imdg.genericmapstore;

import com.colak.imdg.SqlPortable;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import com.hazelcast.nio.serialization.genericrecord.GenericRecord;
import com.hazelcast.nio.serialization.genericrecord.GenericRecordBuilder;
import com.hazelcast.scheduledexecutor.IScheduledExecutorService;

import java.util.concurrent.TimeUnit;

public class GenericMapStoreCreator {

    // Map name has to be the same name in hazelcast.xml
    private static final String MAP_NAME = "dbmap";

    public static void createDbMap(HazelcastInstance hazelcastInstance) {

        SqlPortable.beforeClass(hazelcastInstance);

        IMap<Integer, GenericRecord> map = hazelcastInstance.getMap(MAP_NAME);
        for (int i = 0; i < 1_000; i++) {
            String value = "message" + i;

            GenericRecord genericRecord = GenericRecordBuilder.compact("Person")
                    .setInt32("id", i)
                    .setString("name", value)
                    .setString("ssn", value)
                    .build();

            map.put(i, genericRecord);
        }
    }

    private static void cleanDbMap(HazelcastInstance hazelcastInstance) {
        IMap<Integer, GenericRecord> map = hazelcastInstance.getMap(MAP_NAME);
        map.clear();
    }

    public static void scheduleCleanDbMap(HazelcastInstance hazelcastInstance) {
        IScheduledExecutorService scheduledExecutorService = hazelcastInstance
                .getScheduledExecutorService("executorService1");

        scheduledExecutorService.schedule(() -> GenericMapStoreCreator.cleanDbMap(hazelcastInstance),
                10, TimeUnit.SECONDS);

    }
}