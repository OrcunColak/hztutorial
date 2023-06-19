package com.colak.jet.jdbc;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.jet.JetService;
import com.hazelcast.jet.Job;
import com.hazelcast.jet.config.JobConfig;
import com.hazelcast.jet.pipeline.BatchSource;
import com.hazelcast.jet.pipeline.Pipeline;
import com.hazelcast.jet.pipeline.Sinks;
import com.hazelcast.jet.pipeline.Sources;
import com.hazelcast.map.IMap;

import java.io.File;
import java.net.URL;

public class JdbcToIMapJob {

    private static final String URL = "jdbc:mysql://mysql:3306/db?user=root&password=root";
    private static final String QUERY = "SELECT * FROM myworker";

    private static final String MAP_NAME = "my-map";

    public static void jdbc(HazelcastInstance hazelcastInstance) {


        JetService jet = hazelcastInstance.getJet();

        try {
            JobConfig jobConfig = new JobConfig();

            File file = new File("mysql-connector-j-8.0.33.jar");
            URL jarResource = file.toURI().toURL();
            jobConfig.addJar(jarResource);
            jobConfig.addClass(JdbcToIMapJob.class, Worker.class, ResultFunction.class);

            Pipeline pipeline = buildPipeline();
            Job job = jet.newJob(pipeline, jobConfig);
            job.join();

            IMap<Integer, String> map = hazelcastInstance.getMap(MAP_NAME);
            System.out.println("map = " + map.get(1));


        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private static Pipeline buildPipeline() {
        Pipeline pipeline = Pipeline.create();
        BatchSource<Worker> source = Sources.jdbc(URL,
                QUERY,
                ResultFunction::mapOutput);


        pipeline.readFrom(source)
                .writeTo(Sinks.map(MAP_NAME, Worker::getId, Worker::getName));
        return pipeline;
    }
}
