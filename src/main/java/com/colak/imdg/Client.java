package com.colak.imdg;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;

import java.util.Map;

public class Client {

    public static void main(String[] args) {
        ClientConfig config = new ClientConfig();
        HazelcastInstance hazelcastInstanceClient = HazelcastClient.newHazelcastClient(config);
        Map<Long, String> map = hazelcastInstanceClient.getMap("data");
        for (Map.Entry<Long, String> entry : map.entrySet()) {
            System.out.printf("Key: %d, Value: %s%n", entry.getKey(), entry.getValue());
        }
        System.out.println("Client finished");
    }
}
