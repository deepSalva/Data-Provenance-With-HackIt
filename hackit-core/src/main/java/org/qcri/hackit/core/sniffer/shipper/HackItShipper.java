package org.qcri.hackit.core.sniffer.shipper;

import org.qcri.hackit.core.sniffer.shipper.receiver.HackItReceiver;
import org.qcri.hackit.core.sniffer.shipper.sender.HackItSender;

import java.io.Serializable;
import java.util.Iterator;

public abstract class HackItShipper<T, ST, Sender extends HackItSender<ST>, Receiver extends HackItReceiver<T>> implements Iterator<T>, Serializable {

    protected Sender sender_instance;
    protected Receiver receiver_instance;

    protected abstract Sender createSenderInstance();
    protected abstract Receiver createReceiverInstance();

    /** Connect with the a Message queue service*/
    public void publish(ST value){
        if(this.sender_instance == null){
            throw new RuntimeException("The Sender of the Shipper is not instanciated");
        }
        this.sender_instance.send(value);
    }

    /** To subscribe as a producer */
    public void subscribeAsProducer(){
        this.sender_instance = this.createSenderInstance();
        this.sender_instance.init();
    }

    public void subscribeAsProducer(String... topic){
        this.subscribeAsProducer("default", topic);
    }

    public void subscribeAsProducer(String metatopic, String... topic){
        this.subscribeAsProducer();
        ((HackItPublishSubscribedProtocol)this.sender_instance)
                .addExchange(metatopic)
                .addTopic(topic)
        ;
    }

    /** Close connection */
    public void unsubscribeAsProducer(){
        if( this.sender_instance == null) return;
        this.sender_instance.close();
    }

    /** To subscribe/unsubscribe as a consumer
     * metatopic correspond to EXCHANGE_NAME
     * topics correspond to bindingKeys
     * */
    public void subscribeAsConsumer(){
        this.receiver_instance = this.createReceiverInstance();
        this.receiver_instance.init();
    }
    public void subscribeAsConsumer(String... topic){
        this.subscribeAsProducer("default", topic);
    }
    public void subscribeAsConsumer(String metatopic, String... topic){
        this.subscribeAsConsumer();
        ((HackItPublishSubscribedProtocol)this.receiver_instance)
                .addExchange(metatopic)
                .addTopic(topic)
        ;
    }

    /** Close connection */
    public void unsubscribeAsConsumer() {
        if( this.receiver_instance == null) return;
        this.receiver_instance.close();
    }

    public void close(){
        this.unsubscribeAsConsumer();
        this.unsubscribeAsProducer();
    }

    @Override
    public abstract boolean hasNext();

    @Override
    public abstract T next();

    public Iterator<T> getNexts(){
        if( this.receiver_instance == null){
            throw new RuntimeException("The Receiver of the Shipper is not instanciated");
        }
        return this.receiver_instance.getElements();
    }
}
