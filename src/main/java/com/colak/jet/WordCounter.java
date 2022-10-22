package com.colak.jet;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.jet.JetService;
import com.hazelcast.jet.Job;
import com.hazelcast.jet.pipeline.Pipeline;
import com.hazelcast.jet.pipeline.Sinks;
import com.hazelcast.jet.pipeline.test.TestSources;

public class WordCounter {


    public void countWord(HazelcastInstance hazelcastInstance) {
        Pipeline pipeline = Pipeline.create();
        pipeline.readFrom(TestSources.items("the", "quick", "brown", "fox"))
                .map(String::toUpperCase)
                .writeTo(Sinks.logger());

        JetService jet = hazelcastInstance.getJet();

        try {
            Job job = jet.newJob(pipeline);
            job.join();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

}
