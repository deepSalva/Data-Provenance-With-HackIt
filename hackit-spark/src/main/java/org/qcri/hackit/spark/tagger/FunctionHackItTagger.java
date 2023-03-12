package org.qcri.hackit.spark.tagger;

import org.qcri.hackit.core.tuple.HackItTuple;
import org.qcri.hackit.core.tagger.HackItTagger;
import org.apache.spark.api.java.function.Function;

public class FunctionHackItTagger<K, I, O> extends HackItTagger implements Function<HackItTuple<K, I>, HackItTuple<K, O>> {

    private Function<I, O> function;

    public FunctionHackItTagger(Function<I, O> function) {
        this.function = function;
    }

    @Override
    public HackItTuple<K, O> call(HackItTuple<K, I> v1) throws Exception {
        this.preTaggingTuple(v1);
        O result = this.function.call(v1.getValue());
        return this.postTaggingTuple(v1, result);
    }
}
