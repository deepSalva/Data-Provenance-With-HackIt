package org.qcri.hackit.core.sniffer.clone;

import org.apache.commons.lang.SerializationUtils;

import java.io.Serializable;

public class DefaultCloner<I> implements HackItCloner<I, byte[]> {
    @Override
    public byte[] clone(I input) {
        return SerializationUtils.serialize((Serializable) input);
    }
}
