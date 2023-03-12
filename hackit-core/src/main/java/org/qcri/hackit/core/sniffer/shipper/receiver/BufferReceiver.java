package org.qcri.hackit.core.sniffer.shipper.receiver;

import java.io.Serializable;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class BufferReceiver<T> implements Serializable {
    //TODO implement the doble buffering
    private transient Queue<T> queue;


    //TODO implement the server to receive the messages
    public boolean start(){
        return true;
    }

    //TODO registrer on the rest of the worker
    public boolean register(){
        return true;
    }

    public boolean existQueue(){
        return false;
    }

    public void put(T value){
        if(this.queue == null){
            this.queue = new LinkedBlockingQueue<>();
        }
        this.queue.add(value);
    }

}
