package org.qcri.hackit.core.sniffer.shipper.sender;

import java.io.Serializable;

public interface HackItSender<T> extends Serializable {

    public void init();

    public void send(T value);

    public void close();
}
