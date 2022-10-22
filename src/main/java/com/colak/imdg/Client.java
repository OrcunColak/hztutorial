package com.colak.imdg;

import com.colak.jet.WordCounter;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class Client {

    private static final Logger logger = LoggerFactory.getLogger(Client.class);

    public static void main(String[] args) {
        ClientConfig config = new ClientConfig();
        HazelcastInstance hazelcastInstanceClient = HazelcastClient.newHazelcastClient(config);
        Map<Long, String> map = hazelcastInstanceClient.getMap("data");
        for (Map.Entry<Long, String> entry : map.entrySet()) {
            logger.info("Key: {}, Value: {}%n", entry.getKey(), entry.getValue());
        }
        logger.info("Client finished iterating map");

        WordCounter wordCounter = new WordCounter();
        wordCounter.countWord(hazelcastInstanceClient);

        logger.info("Client finished submitting job");

        hazelcastInstanceClient.shutdown();
    }
}
