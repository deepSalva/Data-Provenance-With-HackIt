package org.qcri.hackit.shipper.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.apache.commons.lang3.SerializationUtils;
import org.qcri.hackit.core.sniffer.shipper.HackItShipper;
import org.qcri.hackit.core.sniffer.shipper.receiver.HackItReceiver;
import org.qcri.hackit.core.sniffer.shipper.sender.HackItSender;
import org.qcri.hackit.core.tuple.HackItTuple;
import org.qcri.hackit.shipper.rabbitmq.receiver.ReceiverMultiChannelRabbitMQ;
import org.qcri.hackit.shipper.rabbitmq.sender.SenderMultiChannelRabbitMQ;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.TimeoutException;

/** Direct is faster but there is not multiple categories of topics, just a word
 *  In other words you publish based on a word, all the consumers who listen that word will fill the message in their queues
 * */
public class HachitShipperDirectRabbitMQ<K, T, ST, Sender extends HackItSender<ST>, Receiver extends HackItReceiver<HackItTuple<K, T>>> extends HackItShipper<HackItTuple<K, T>, ST, Sender, Receiver> {

    private transient ConnectionFactory connectionFactory = null;

    /** Consumer Info */
    private Connection consumeConnection;
    private Channel consumeChannel;
    private String consumeExchangeName;
    private String queueName;


    @Override
    protected Sender createSenderInstance() {
        return (Sender) new SenderMultiChannelRabbitMQ(this.connect());
    }

    @Override
    protected Receiver createReceiverInstance() {
        return (Receiver) new ReceiverMultiChannelRabbitMQ(this.connect());
    }

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public HackItTuple<K, T> next() {
        return null;
    }

    public Connection connect(){
        if(this.connectionFactory == null){
            Properties prop = new Properties();
            InputStream is = null;

            try {
                //is = new FileInputStream("../../resources/rabbitmq-config.properties");
                is = new FileInputStream("/home/savi01/dfki/BDAPRO/bdapro-ss20-dataprov/hackit-src/hackit-shipper/hackit-shipper-rabbitmq/src/main/resources/rabbitmq-config.properties");
                prop.load(is);
            } catch(IOException e) {
                throw new RuntimeException(e);
            }

            this.connectionFactory= new ConnectionFactory();
            this.connectionFactory.setUsername(prop.getProperty("username"));
            this.connectionFactory.setPassword(prop.getProperty("password"));
            this.connectionFactory.setVirtualHost(prop.getProperty("virtualhost"));
            this.connectionFactory.setHost(prop.getProperty("host"));
            this.connectionFactory.setPort(Integer.parseInt(prop.getProperty("port")));
        }

        try {
            return this.connectionFactory.newConnection();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }

        return null;
    }
}