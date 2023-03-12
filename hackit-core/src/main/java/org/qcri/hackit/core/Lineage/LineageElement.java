package org.qcri.hackit.core.Lineage;

import java.io.Serializable;

public interface LineageElement<I, O>  extends Serializable{
    public I getInput();
    public O getOutput();
}