package com.colak.imdg;

import com.colak.jet.WordCounterJob;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class Client {

    private static final Logger logger = LoggerFactory.getLogger(Client.class);

    public static void main(String[] args) {
        ClientConfig clientConfig = new ClientConfig();
        HazelcastInstance hazelcastInstanceClient = HazelcastClient.newHazelcastClient(clientConfig);

        iterateSimpleMap(hazelcastInstanceClient);

        submitWordCounterJob(hazelcastInstanceClient);

        logger.info("Client finished submitting job");

        hazelcastInstanceClient.shutdown();
    }

    private static void submitWordCounterJob(HazelcastInstance hazelcastInstanceClient) {
        WordCounterJob wordCounterJob = new WordCounterJob();
        wordCounterJob.countWord(hazelcastInstanceClient);
    }

    private static void iterateSimpleMap(HazelcastInstance hazelcastInstanceClient) {
        Map<Long, String> map = hazelcastInstanceClient.getMap("data");
        for (Map.Entry<Long, String> entry : map.entrySet()) {
            logger.info("Key: {}, Value: {}%n", entry.getKey(), entry.getValue());
        }
        logger.info("Client finished iterating map");
    }
}
