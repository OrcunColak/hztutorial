package com.colak.imdg;

import com.colak.jet.WordCounter;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.flakeidgen.FlakeIdGenerator;
import com.hazelcast.nio.serialization.genericrecord.GenericRecord;
import com.hazelcast.nio.serialization.genericrecord.GenericRecordBuilder;
import org.example.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;


public class Server {

    private static final Logger logger = LoggerFactory.getLogger(Server.class);


    public static void main(String[] args) {

        HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance();

        PrepareDatabase.prepareDatabase(hazelcastInstance);

        logger.info("Name of the instance: {}", hazelcastInstance.getName());

        createDbMap(hazelcastInstance);
        logger.info("Server created dbmap");

        createSimpleMap(hazelcastInstance);
        logger.info("Server created simple map");

        createPersonMap(hazelcastInstance);
        logger.info("Server created person map");

        WordCounter wordCounter = new WordCounter();
        wordCounter.countWord(hazelcastInstance);
        logger.info("Server ready");
    }

    private static void createDbMap(HazelcastInstance hazelcastInstance) {


        SqlPortable.beforeClass(hazelcastInstance);

        Map<Integer, GenericRecord> map = hazelcastInstance.getMap("dbmap");
        for (int i = 0; i < 10; i++) {
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
        Map<Long, String> map = hazelcastInstance.getMap("data");
        FlakeIdGenerator idGenerator = hazelcastInstance.getFlakeIdGenerator("newid");
        for (int i = 0; i < 10; i++) {
            long key = idGenerator.newId();
            String value = "message" + i;
            map.put(key, value);
        }
    }

    private static void createPersonMap(HazelcastInstance hazelcastInstance) {
        Map<Long, Person> map = hazelcastInstance.getMap("person");
        FlakeIdGenerator idGenerator = hazelcastInstance.getFlakeIdGenerator("newid");
        for (int i = 0; i < 10; i++) {
            long key = idGenerator.newId();
            String name = "message" + i;
            Person person = new Person(key, name);
            map.put(key, person);
        }
    }
}
