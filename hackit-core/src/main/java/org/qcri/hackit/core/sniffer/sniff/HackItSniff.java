package org.qcri.hackit.core.sniffer.sniff;

import java.io.Serializable;

public interface HackItSniff<I> extends Serializable {

    public boolean sniff(I input);

}
