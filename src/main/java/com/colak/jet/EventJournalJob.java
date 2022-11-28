package com.colak.jet;

import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.jet.JetService;
import com.hazelcast.jet.pipeline.JournalInitialPosition;
import com.hazelcast.jet.pipeline.Pipeline;
import com.hazelcast.jet.pipeline.Sinks;
import com.hazelcast.jet.pipeline.Sources;
import com.hazelcast.jet.pipeline.StreamSource;

import java.util.Map;

public class EventJournalJob {

    public void journal(HazelcastInstance hazelcastInstance) {

        Pipeline pipeline = Pipeline.create();
        StreamSource<Map.Entry<Object, Object>> streamSource = Sources.remoteMapJournal("mapstoremap",
                new ClientConfig(),
                JournalInitialPosition.START_FROM_OLDEST);

        pipeline.readFrom(streamSource)
                .withoutTimestamps()
                .setLocalParallelism(2)
                .writeTo(Sinks.logger());


        JetService jet = hazelcastInstance.getJet();

        try {
            jet.newJob(pipeline);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
