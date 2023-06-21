package com.colak.jet.jdbc;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.jet.JetService;
import com.hazelcast.jet.Job;
import com.hazelcast.jet.config.JobConfig;
import com.hazelcast.jet.pipeline.Pipeline;
import com.hazelcast.map.IMap;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.nio.file.Files;
import java.util.function.Supplier;

public class JdbcToIMapJob {


    static Class<?> pipeLineClass;
    static Supplier<Pipeline> pipelineSupplier;

    public static void jdbc(HazelcastInstance hazelcastInstance)
            throws ClassNotFoundException, NoSuchMethodException, IOException,
            InvocationTargetException, InstantiationException, IllegalAccessException {

        File sourceFile = new File("D:/hztutorial/hztutorial/src/main/resources/com/colak/jet/jdbc/resource/postgre_to_imap.txt");

        String allLines = new String (Files.readAllBytes(sourceFile.toPath()));


        String className = "com.colak.jet.jdbc.PosgreToIMap";
        pipeLineClass = DynamicCompiler.compileForJava11(className,allLines);

        pipelineSupplier = (Supplier<Pipeline>) pipeLineClass.getDeclaredConstructor().newInstance();

        destroyMap(hazelcastInstance);


        JetService jet = hazelcastInstance.getJet();

        try {
            JobConfig jobConfig = new JobConfig();

            File file = new File("mysql-connector-j-8.0.33.jar");
            URL jarResource = file.toURI().toURL();
            jobConfig.addJar(jarResource);
            jobConfig.addClass(pipeLineClass);

            Pipeline pipeline = pipelineSupplier.get();
            Job job = jet.newJob(pipeline, jobConfig);
            job.join();

            IMap<Integer, String> map = hazelcastInstance.getMap("my-map");
            System.out.println("map = " + map.get(1));


        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private static void destroyMap(HazelcastInstance hazelcastInstance) {
        IMap<Object, Object> map = hazelcastInstance.getMap("my-map");
        map.destroy();
    }


}
