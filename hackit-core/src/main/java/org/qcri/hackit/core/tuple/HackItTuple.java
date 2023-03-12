package org.qcri.hackit.core.tuple;

import org.qcri.hackit.core.tags.HackItTag;
import org.qcri.hackit.core.action.HackItActionVector;

import java.io.Serializable;
import java.util.Iterator;

public class HackItTuple<K, T> implements Serializable, HackItActionVector {
    private static HackItTupleHeaderBuilder BUILDER;
    private HackItTupleHeader<K> header;
    private T value;

    static {
        BUILDER = new HackItTupleHeaderBuilder();
    }

    public HackItTuple(T value){
        this.header = BUILDER.generateHeader();
        this.value = value;
    }

    public HackItTuple(HackItTupleHeader<K> header, T value){
        this.header = header;
        this.value = value;
    }

    public HackItTupleHeader<K> getHeader(){
        return this.header;
    }

    public K getKey(){
        return this.header.getId();
    }

    public T getValue(){
        return this.value;
    }

    public void addTag(HackItTag tag){
        this.header.addTag(tag);
    }

    public Iterator<HackItTag> getTags(){
        return this.header.iterate();
    }

    @Override
    public String toString() {
        return "HackItTuple{" +
                "header=" + header +
                ", value=" + value +
                '}';
    }


    @Override
    public boolean hasCallback() {
        return this.getHeader().hasCallback();
    }

    @Override
    public boolean isHaltJob() {
        return this.getHeader().isHaltJob();
    }

    @Override
    public boolean isSendOut() {
        return this.getHeader().isSendOut();
    }

    @Override
    public boolean isSkip() {
        return this.getHeader().isSkip();
    }
}
