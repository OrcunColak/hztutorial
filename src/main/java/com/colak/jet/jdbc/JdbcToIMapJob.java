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
import java.sql.ResultSet;
import java.sql.SQLException;

public class JdbcToIMapJob {

    private static final String URL = "jdbc:mysql://mysql:3306/db?user=root&password=root";
    private static final String QUERY = "SELECT * FROM myworker";

    private static final String MAP_NAME = "my-map";

    public static void jdbc(HazelcastInstance hazelcastInstance) {
        destroyMap(hazelcastInstance);


        JetService jet = hazelcastInstance.getJet();

        try {
            JobConfig jobConfig = new JobConfig();

            File file = new File("mysql-connector-j-8.0.33.jar");
            URL jarResource = file.toURI().toURL();
            jobConfig.addJar(jarResource);
            jobConfig.addClass(JdbcToIMapJob.class);

            Pipeline pipeline = buildPipeline();
            Job job = jet.newJob(pipeline, jobConfig);
            job.join();

            IMap<Integer, String> map = hazelcastInstance.getMap(MAP_NAME);
            System.out.println("map = " + map.get(1));


        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private static void destroyMap(HazelcastInstance hazelcastInstance) {
        IMap<Object, Object> map = hazelcastInstance.getMap(MAP_NAME);
        map.destroy();
    }

    public static Worker mapOutput(ResultSet resultSet) throws SQLException {
        return new Worker(resultSet.getInt("id"), resultSet.getString("name"));
    }

    private static Pipeline buildPipeline() {
        Pipeline pipeline = Pipeline.create();
        BatchSource<Worker> source = Sources.jdbc(URL,
                QUERY,
                resultSet -> new Worker(resultSet.getInt("id"), resultSet.getString("name")));


        pipeline.readFrom(source)
                .writeTo(Sinks.map(MAP_NAME, Worker::getId, Worker::getName));
        return pipeline;
    }

    public static class Worker {

        int id;
        String name;

        public Worker(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

}
