package org.qcri.hackit.core.wrapper;

public abstract class HackItTagger<I, O> {

    public abstract O apply(I input);
}
