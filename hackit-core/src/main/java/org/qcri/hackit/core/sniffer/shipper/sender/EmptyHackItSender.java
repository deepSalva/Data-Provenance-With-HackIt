package org.qcri.hackit.core.sniffer.shipper.sender;

public class EmptyHackItSender<T> implements HackItSender<T> {

    @Override
    public void init() {

    }

    @Override
    public void send(T value) {}

    @Override
    public void close() {

    }

}
