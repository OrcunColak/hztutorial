package com.colak.imdg;

import com.colak.jet.WordCounterJob;
import com.colak.jet.jdbc.JdbcToIMapJob;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.ClientTpcConfig;
import com.hazelcast.core.HazelcastInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Client {

    private static final Logger logger = LoggerFactory.getLogger(Client.class);

    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, IOException,
            InvocationTargetException, InstantiationException, IllegalAccessException {
        ClientConfig clientConfig = new ClientConfig();

        ClientTpcConfig tpcConfig = clientConfig.getTpcConfig();
        List<String> memberList = new ArrayList<>();
        memberList.add("127.0.0.1:5900");

        HazelcastInstance hazelcastInstanceClient = HazelcastClient.newHazelcastClient(clientConfig);

        submitJdbc(hazelcastInstanceClient);

        /*iterateSimpleMap(hazelcastInstanceClient);


        submitWordCounterJob(hazelcastInstanceClient);*/

        logger.info("Client finished submitting job");

        hazelcastInstanceClient.shutdown();
    }

    private static void submitJdbc(HazelcastInstance hazelcastInstanceClient)
            throws ClassNotFoundException, NoSuchMethodException,
            InvocationTargetException, InstantiationException, IllegalAccessException, IOException {
        JdbcToIMapJob.jdbc(hazelcastInstanceClient);
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
