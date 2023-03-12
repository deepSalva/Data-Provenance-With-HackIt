package org.qcri.hackit.core.tagger;

public interface TaggerFunction<T> {

    public T execute();

    public String getName();
}
