package com.colak.jet.jdbc;

import com.hazelcast.collection.IList;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.jet.JetService;
import com.hazelcast.jet.Job;
import com.hazelcast.jet.config.JobConfig;
import com.hazelcast.jet.kafka.connect.KafkaConnectSources;
import com.hazelcast.jet.pipeline.Pipeline;
import com.hazelcast.jet.pipeline.Sinks;
import com.hazelcast.jet.pipeline.StreamSource;
import com.hazelcast.jet.pipeline.StreamStage;
import org.apache.kafka.connect.connector.ConnectRecord;

import java.io.File;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class KafkaConnectDataGeneratorJob {

    public static void kafkaConnectDataGen(HazelcastInstance hazelcastInstance) throws Exception {

        Properties randomProperties = new Properties();
        randomProperties.setProperty("name", "jdbc-connector");
        randomProperties.setProperty("connector.class", "io.confluent.connect.jdbc.JdbcSourceConnector");
        randomProperties.setProperty("mode", "incrementing");
        randomProperties.setProperty("connection.url", "jdbc:mysql://mysql:3306/db");
        randomProperties.setProperty("connection.user", "root");
        randomProperties.setProperty("connection.password", "root");
        randomProperties.setProperty("incrementing.column.name", "id");
        randomProperties.setProperty("table.whitelist", "myworker");

        String listName = "testResults";

        Pipeline pipeline = Pipeline.create();
        StreamSource<Integer> source = KafkaConnectSources.connect(randomProperties, ConnectRecord::hashCode);
        StreamStage<Integer> streamStage = pipeline.readFrom(source)
                .withoutTimestamps()
                .setLocalParallelism(1);

        streamStage.writeTo(Sinks.list(listName));


        JobConfig jobConfig = new JobConfig();
        File file = new File("confluentinc-kafka-connect-jdbc-10.7.3.zip");
        jobConfig.addJarsInZip(file);
        jobConfig.addClass(KafkaConnectDataGeneratorJob.class);

        JetService jet = hazelcastInstance.getJet();
        Job job = jet.newJob(pipeline, jobConfig);
        TimeUnit.SECONDS.sleep(30);

        try {
            job.cancel();
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        IList<Object> list = hazelcastInstance.getList(listName);
        System.out.println(list.size());
    }

}
