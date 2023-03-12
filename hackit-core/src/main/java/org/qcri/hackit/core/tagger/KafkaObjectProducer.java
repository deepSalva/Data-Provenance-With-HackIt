package org.qcri.hackit.core.tagger;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.ByteArraySerializer;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;
import java.util.Properties;

public class KafkaObjectProducer{
    private final static String TOPIC = "lineagetest2";
    private final static String BOOTSTRAP_SERVERS = "localhost:9092,localhost:9093,localhost:9094";

    private Producer<byte[], byte[]> createProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "KafkaExampleProducer");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class.getName());
        return new KafkaProducer<byte[], byte[]>(props);
    }

    public void produceToKafka(LinkedList<Long> lineageList) throws Exception {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(out);
        os.writeObject(lineageList);
        byte[] serialForm = out.toByteArray();

        long time = System.currentTimeMillis();
        final Producer<byte[], byte[]> producer = createProducer();
        try {
            final ProducerRecord<byte[], byte[]> record = new ProducerRecord<byte[], byte[]>(
                    TOPIC,
                    null,
                    serialForm);
            RecordMetadata metadata = producer.send(record).get();
            long elapsedTime = System.currentTimeMillis() - time;
            System.out.printf("sent record(value=%s) " + "time=%d\n",
                    metadata, elapsedTime);
        } finally {
            producer.flush();
            producer.close();
        }
    }
}

