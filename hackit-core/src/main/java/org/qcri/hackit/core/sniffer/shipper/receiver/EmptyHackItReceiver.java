package org.qcri.hackit.core.sniffer.shipper.receiver;

import java.util.Collections;
import java.util.Iterator;

public class EmptyHackItReceiver<T> extends HackItReceiver<T> {

    @Override
    public void init() {}

    @Override
    public Iterator<T> getElements() {
        return (Iterator<T>) Collections.emptyIterator();
    }

    @Override
    public void close() {

    }
}
