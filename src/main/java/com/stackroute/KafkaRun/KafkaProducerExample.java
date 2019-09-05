package com.stackroute.KafkaRun;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.LongSerializer;
//import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.SpringApplication;

import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit ;



import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.KeyValueStore;
import java.util.Arrays;

public class KafkaProducerExample {

    public static void main(String[] args) throws Exception {


        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "wordcount-application");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "0.0.0.0:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, String> textLines = builder.stream("TextLinesTopic" +
                "");
        KTable<String, Long> wordCounts = textLines
                .flatMapValues(textLine -> Arrays.asList(textLine.toLowerCase().split("\\W+")))

                .groupBy((key, word) -> {
                    System.out.println(key + word);
                    return  word;
                })
                .count(Materialized.<String, Long, KeyValueStore<Bytes, byte[]>>as("counts-store"));
        wordCounts.toStream().to("WordsWithCountsTopic", Produced.with(Serdes.String(), Serdes.Long()));

        KafkaStreams streams = new KafkaStreams(builder.build(), props);
        streams.start();
    }
}



        //SpringApplication.run(KafkaRunApplication.class, args);
//        if (args.length == 0) {
//            runProducer(5);
//        } else {
//            runProducer(Integer.parseInt(args[0]));
//        }
//    }
//
//    private final static String TOPIC = "my-example-topic";
//    private final static String BOOTSTRAP_SERVERS =
//            "localhost:9092,localhost:9093,localhost:9094";
//
//    private static Producer<Long, String> createProducer() {
//        Properties props = new Properties();
//        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
//                BOOTSTRAP_SERVERS);
//        props.put(ProducerConfig.CLIENT_ID_CONFIG, "KafkaExampleProducer");
//        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
//                LongSerializer.class.getName());
//        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
//                StringSerializer.class.getName());
//        return new KafkaProducer<>(props);
//    }
//
//    static void runProducer(final int sendMessageCount) throws Exception {
//        final Producer<Long, String> producer = createProducer();
//        long time = System.currentTimeMillis();
//        final CountDownLatch countDownLatch = new CountDownLatch(sendMessageCount);
//
//        try {
//            for (long index = time; index < time + sendMessageCount; index++) {
//                final ProducerRecord<Long, String> record =
//                        new ProducerRecord<>(TOPIC, index,
//                                "Hello Mom " + index);
//                producer.send(record, (metadata, exeception) -> {
//                    long elapsedTime = System.currentTimeMillis() - time;
//                    if (metadata != null) {
//                        System.out.printf("sent record(key=%s value=%s) " +
//                                        "meta(partition=%d, offset=%d) time=%d\n",
//                                record.key(), record.value(), metadata.partition(),
//                                metadata.offset(), elapsedTime);
//                    } else {
//                        //exception.printStackTrace();
//                    }
//                    countDownLatch.countDown();
//
//                });
//                //RecordMetadata metadata = producer.send(record).get();
//            }
//            countDownLatch.await(25, TimeUnit.SECONDS);
//
//        } finally {
//            producer.flush();
//            producer.close();
//        }
//    }
//
//
//}
//
