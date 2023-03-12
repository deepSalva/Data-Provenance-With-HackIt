package org.qcri.hackit.core.sniffer.shipper.receiver;

import java.io.Serializable;
import java.util.Iterator;

public abstract class HackItReceiver<T> implements Serializable {

    private transient BufferReceiver<T> bufferReceive;

    public abstract void init();

    public abstract Iterator<T> getElements();

    public abstract void close();

}
