package org.qcri.hackit.flink.Tagger;

import org.apache.flink.api.common.functions.GroupReduceFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.util.Collector;
import org.qcri.hackit.core.tagger.HackItTagger;
import org.qcri.hackit.core.tuple.HackItTuple;

import java.util.Iterator;
import java.util.LinkedList;

public class MappingReduceTagger<T> extends HackItTagger implements GroupReduceFunction<HackItTuple<Long, T>, HackItTuple<Long, T>> {

    private ReduceFunction<T> reduceFunction;
    private LinkedList<Long> inputSet;

    public MappingReduceTagger(ReduceFunction<T> reduceFunction) {
        this.reduceFunction = reduceFunction;
        inputSet = new LinkedList<>();
    }

    @Override
    public void reduce(Iterable<HackItTuple<Long, T>> values, Collector<HackItTuple<Long, T>> out) throws Exception {
        Iterator<HackItTuple<Long, T>> iterator = values.iterator();
        HackItTuple<Long, T> tuple = iterator.next();
        T result = tuple.getValue();
        inputSet.add(tuple.getHeader().getId());
        while (iterator.hasNext()){
            tuple = iterator.next();
            inputSet.add(tuple.getHeader().getId());

            result = reduceFunction.reduce(tuple.getValue(), result);
        }

        out.collect(this.postReduceTuple(inputSet, result));
    }
}