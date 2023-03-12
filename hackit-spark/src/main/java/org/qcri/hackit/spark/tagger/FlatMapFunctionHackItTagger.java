package org.qcri.hackit.spark.tagger;

import org.qcri.hackit.core.tagger.HackItTagger;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.qcri.hackit.core.tuple.HackItTuple;

import java.util.Iterator;

public class FlatMapFunctionHackItTagger<K, I, O> extends HackItTagger implements FlatMapFunction<HackItTuple<K, I>, HackItTuple<K, O>> {

    private FlatMapFunction<I, O> function;

    public FlatMapFunctionHackItTagger(FlatMapFunction<I, O> function) {
        this.function = function;
    }


    @Override
    public Iterator<HackItTuple<K, O>> call(HackItTuple<K, I> kiHackItTuple) throws Exception {
        this.preTaggingTuple(kiHackItTuple);
        Iterator<O> result = this.function.call(kiHackItTuple.getValue());
        return this.postTaggingTuple(kiHackItTuple, result);
    }
}
