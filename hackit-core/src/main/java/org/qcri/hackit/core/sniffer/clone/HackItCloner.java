package org.qcri.hackit.core.sniffer.clone;


import java.io.Serializable;

public interface HackItCloner<I, O> extends Serializable {

    public O clone(I input);
}
