package com.colak.imdg;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.flakeidgen.FlakeIdGenerator;
import org.example.Person;

import java.util.Map;


public class Server {
    public static void main(String[] args) {
        HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance();

        System.out.println(String.format("Name of the instance: %s", hazelcastInstance.getName()));

        createSimpleMap(hazelcastInstance);

        createPersonMap(hazelcastInstance);

        System.out.println("Server ready");
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
