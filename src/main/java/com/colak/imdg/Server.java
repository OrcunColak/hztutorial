package com.colak.imdg;

import com.colak.jet.EventJournalJob;
import com.colak.jet.WordCounterJob;
import com.hazelcast.config.Config;
import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.config.ReplicatedMapConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IExecutorService;
import com.hazelcast.flakeidgen.FlakeIdGenerator;
import com.hazelcast.map.IMap;
import com.hazelcast.nio.serialization.genericrecord.GenericRecord;
import com.hazelcast.nio.serialization.genericrecord.GenericRecordBuilder;
import com.hazelcast.replicatedmap.ReplicatedMap;
import com.hazelcast.scheduledexecutor.TaskUtils;
import org.example.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;


public class Server {

    private static final Logger logger = LoggerFactory.getLogger(Server.class);


    public static void main(String[] args) {

        HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance();

        //PrepareDatabase.prepareDatabase(hazelcastInstance);

        logger.info("Name of the instance: {}", hazelcastInstance.getName());

        createEventJournalJob(hazelcastInstance);
        createExecutorService(hazelcastInstance);

        createMapStore(hazelcastInstance);

        createReplicatedMap(hazelcastInstance);

        createDbMap(hazelcastInstance);
        logger.info("Server created dbmap");

        createSimpleMap(hazelcastInstance);
        logger.info("Server created simple map");

        createPersonMap(hazelcastInstance);
        logger.info("Server created person map");

        createWordCounterJob(hazelcastInstance);
        logger.info("Server ready");
    }

    private static void createEventJournalJob(HazelcastInstance hazelcastInstance) {
        EventJournalJob eventJournal = new EventJournalJob();
        eventJournal.journal(hazelcastInstance);
    }
    private static void createExecutorService(HazelcastInstance hazelcastInstance) {

        IExecutorService executorService = hazelcastInstance.getExecutorService("executorService1");
        Runnable myRunnable = TaskUtils.named("orcun", new OneSecondSleepingRunnable());

        Callable<Integer> myCallable = TaskUtils.named("orcun", new OneSecondSleepingCallable());
        Future<Integer> integerFuture = executorService.submit(myCallable);
        try {
            Integer result = integerFuture.get();
            logger.info("Callable Result is : {}", result);
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Exception caught in callable: ", e);
        }

    }

    private static void createReplicatedMap(HazelcastInstance hazelcastInstance) {
        Config config = hazelcastInstance.getConfig();
        ReplicatedMapConfig replicatedMapConfig = config.getReplicatedMapConfig("rogueUsers");

        replicatedMapConfig.setInMemoryFormat(InMemoryFormat.BINARY);
        replicatedMapConfig.setAsyncFillup(true);
        replicatedMapConfig.setStatisticsEnabled(true);
        replicatedMapConfig.setSplitBrainProtectionName("splitbrainprotection-name");

        ReplicatedMap<String, String> replicatedMap = hazelcastInstance.getReplicatedMap("rogueUsers");
        replicatedMap.put("key", "value");

    }

    private static void createMapStore(HazelcastInstance hazelcastInstance) {

        IMap<Long, String> mapStore = hazelcastInstance.getMap("mapstoremap");
        mapStore.put(1L, "test");
        String s = mapStore.get(1L);
        String s1 = mapStore.get(2L);

    }

    private static void createDbMap(HazelcastInstance hazelcastInstance) {


        SqlPortable.beforeClass(hazelcastInstance);

        IMap<Integer, GenericRecord> map = hazelcastInstance.getMap("dbmap");
        for (int i = 0; i < 3; i++) {
            String value = "message" + i;

            GenericRecord genericRecord = GenericRecordBuilder.compact("Person")
                    .setInt32("id", i)
                    .setString("name", value)
                    .setString("ssn", value)
                    .build();

            map.put(i, genericRecord);
        }
    }

    private static void createSimpleMap(HazelcastInstance hazelcastInstance) {
        IMap<Long, String> map = hazelcastInstance.getMap("data");
        FlakeIdGenerator idGenerator = hazelcastInstance.getFlakeIdGenerator("newid");
        for (int i = 0; i < 10; i++) {
            long key = idGenerator.newId();
            String value = "message" + i;
            map.put(key, value);
        }
    }

    private static void createPersonMap(HazelcastInstance hazelcastInstance) {
        IMap<Long, Person> map = hazelcastInstance.getMap("person");
        FlakeIdGenerator idGenerator = hazelcastInstance.getFlakeIdGenerator("newid");
        for (int i = 0; i < 10; i++) {
            long key = idGenerator.newId();
            String name = "message" + i;
            Person person = new Person(key, name);
            map.put(key, person);
        }
    }

    private static void createWordCounterJob(HazelcastInstance hazelcastInstance) {
        WordCounterJob wordCounterJob = new WordCounterJob();
        wordCounterJob.countWord(hazelcastInstance);
    }
}
