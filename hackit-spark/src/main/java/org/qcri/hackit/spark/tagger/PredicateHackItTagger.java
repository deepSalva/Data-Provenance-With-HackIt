package org.qcri.hackit.spark.tagger;

import org.qcri.hackit.core.tuple.HackItTuple;
import org.qcri.hackit.core.tagger.HackItTagger;
import org.apache.spark.api.java.function.Function;

public class PredicateHackItTagger<K, I> extends HackItTagger implements Function<HackItTuple<K, I>, Boolean> {

    private Function<I, Boolean> function;

    public PredicateHackItTagger(Function<I, Boolean> function) {
        this.function = function;
    }

    @Override
    public Boolean call(HackItTuple<K, I> v1) throws Exception {
        this.preTaggingTuple(v1);
        Boolean result = this.function.call(v1.getValue());
        this.postTaggingTuple(v1);
        return result;
    }
}
