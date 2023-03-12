package org.qcri.hackit.core.sniffer.inject;

import java.io.Serializable;
import java.util.Iterator;

public interface HackItInjector<T> extends Serializable {

    public Iterator<T> inject(T element, Iterator<T> iterator);

    public boolean is_skip_element(T element);

    public boolean is_halt_job(T element);

}
