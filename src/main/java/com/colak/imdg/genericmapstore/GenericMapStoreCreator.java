package com.colak.imdg.genericmapstore;

import com.colak.imdg.SqlPortable;
import com.hazelcast.config.Config;
import com.hazelcast.config.DataConnectionConfig;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MapStoreConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import com.hazelcast.mapstore.GenericMapStore;
import com.hazelcast.nio.serialization.genericrecord.GenericRecord;
import com.hazelcast.nio.serialization.genericrecord.GenericRecordBuilder;
import com.hazelcast.scheduledexecutor.IScheduledExecutorService;

import java.util.concurrent.TimeUnit;

// This class creates an IMap using GenericMapStore as read/write store
public class GenericMapStoreCreator {

    // Map name has to be the same name in hazelcast.xml
    static final String MAP_NAME = "dbmap";

    private GenericMapStoreCreator() {
    }

    // Set MapStoreConfig for MAP_NAME
    public static void setConfig(Config config) {
        DataConnectionConfig dataConnectionConfig = new DataConnectionConfig()
                .setName("mysql")
                .setType("jdbc")
                .setProperty("jdbcUrl", "jdbc:mysql://localhost:3306/db?sessionVariables=sql_mode=ANSI_QUOTES")
                .setProperty("username", "root")
                .setProperty("password", "root");
        config.addDataConnectionConfig(dataConnectionConfig);

        MapStoreConfig mapStoreConfig = new MapStoreConfig();
        mapStoreConfig.setClassName(GenericMapStore.class.getName())
                .setProperty("data-connection-ref", "mysql");

        MapConfig mapConfig = new MapConfig(MAP_NAME);
        mapConfig.setMapStoreConfig(mapStoreConfig);
        config.addMapConfig(mapConfig);
    }

    // Populate MAP_NAME
    public static void populateDbMap(HazelcastInstance hazelcastInstance) {
        // Value is Person. It needs to be serializable with GenericRecord
        SqlPortable.beforeClass(hazelcastInstance);

        IMap<Integer, GenericRecord> map = hazelcastInstance.getMap(MAP_NAME);
        for (int i = 0; i < 1; i++) {
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