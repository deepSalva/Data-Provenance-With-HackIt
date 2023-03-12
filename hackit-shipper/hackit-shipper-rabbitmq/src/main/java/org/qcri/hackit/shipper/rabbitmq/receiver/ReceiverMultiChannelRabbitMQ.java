package org.qcri.hackit.shipper.rabbitmq.receiver;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Envelope;
import org.apache.commons.lang3.SerializationUtils;
import org.qcri.hackit.core.sniffer.shipper.HackItPublishSubscribedProtocol;
import org.qcri.hackit.core.sniffer.shipper.receiver.HackItReceiver;
import org.qcri.hackit.core.tuple.HackItTuple;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

public class ReceiverMultiChannelRabbitMQ<K, T> extends HackItReceiver<HackItTuple<K, T>> implements HackItPublishSubscribedProtocol {

    private transient Connection connection;
    private transient Channel channel;
    private transient Thread thread_collecting;
    private transient ArrayList<HackItTuple<K, T>> collection;

    /** Default values */
    private String exchange_name = "default_consumer";
    private String topic_name = "default_consumer";
    private String queue_name= "";


    public ReceiverMultiChannelRabbitMQ(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void init() {
        try {
            this.channel =  this.connection.createChannel();
            this.queue_name = this.channel.queueDeclare().getQueue();
            System.out.println(this.queue_name);
            this.collection = new ArrayList<>();

            final ReceiverMultiChannelRabbitMQ<K, T> thos = this;
            this.thread_collecting = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        channel.basicConsume(queue_name, true, new DefaultConsumer(channel) {
                            @Override
                            public void handleDelivery(String consumerTag,
                                                       Envelope envelope,
                                                       AMQP.BasicProperties properties,
                                                       byte[] body)
                                    throws IOException
                            {
                                HackItTuple<K, T> elem = SerializationUtils.deserialize(body);
                                thos.addElement(elem);
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            this.thread_collecting.run();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Iterator<HackItTuple<K, T>> getElements() {
        return this._getElements();
    }

    @Override
    public void close() {
        try {
            this.thread_collecting.stop();
            this.channel.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

    @Override
    public HackItPublishSubscribedProtocol addTopic(String... topic) {
        if(topic.length > 1) {
            this.topic_name = Arrays.stream(topic).collect(Collectors.joining("."));
        }else {
            this.topic_name = topic[0];
        }
        return this;
    }

    @Override
    public HackItPublishSubscribedProtocol addExchange(String exchange) {
        this.exchange_name = exchange;
        return this;
    }

    public synchronized void addElement(HackItTuple<K, T> element){
        this.collection.add(element);
    }

    private synchronized Iterator<HackItTuple<K, T>> _getElements(){
        if(this.collection.size() == 0){
            return Collections.emptyIterator();
        }
        Iterator<HackItTuple<K, T>> tmp = this.collection.iterator();
        this.collection = new ArrayList<>();
        return tmp;
    }
}
