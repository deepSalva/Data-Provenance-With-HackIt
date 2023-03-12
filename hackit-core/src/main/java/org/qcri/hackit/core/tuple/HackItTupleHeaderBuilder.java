package org.qcri.hackit.core.tuple;

public class HackItTupleHeaderBuilder {


    public HackItTupleHeaderBuilder(){
        //TODO: take from the configuration
    }

    public <K> HackItTupleHeader<K> generateHeader(){
        return (HackItTupleHeader<K>) new HackItTupleHeaderLong();
    }

}
