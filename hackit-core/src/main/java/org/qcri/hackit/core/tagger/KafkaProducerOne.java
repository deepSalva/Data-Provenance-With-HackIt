package org.qcri.hackit.core.tagger;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import scala.Tuple2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

public class KafkaProducerOne<K, T> {
    private final static String TOPIC = "lineageTest1";
    private final static String BOOTSTRAP_SERVERS = "localhost:9092,localhost:9093,localhost:9094";

    private Producer<String, String> createProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "KafkaExampleProducer");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        return new KafkaProducer<String, String>(props);
    }

    public void produceToKafka(K itemOne, T item2) throws Exception {
        final String idsLinked;
        long time = System.currentTimeMillis();
        final Producer<String, String> producer = createProducer();
        try {
            if (item2 instanceof LinkedList) {
                ArrayList<String> concatList = new ArrayList<>();
                for (Long id : (LinkedList<Long>) item2) {
                    concatList.add(String.valueOf(id));
                }
                final String idsConcat = String.join("," , concatList);
                idsLinked = "("+ itemOne +","+ idsConcat +")";
            }else {
                idsLinked = "("+ itemOne +","+ item2 +")";
            }
            final ProducerRecord<String, String> record = new ProducerRecord<String, String>(
                    TOPIC,
                    null,
                    idsLinked);
            RecordMetadata metadata = producer.send(record).get();
            long elapsedTime = System.currentTimeMillis() - time;
            System.out.printf("sent record(value=%s) " + "time=%d\n",
                    record.value(), elapsedTime);
        } finally {
            producer.flush();
            producer.close();
        }
    }
}
