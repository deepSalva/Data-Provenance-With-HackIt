package org.qcri.hackit.flink.Tagger;

import org.apache.flink.api.common.functions.MapFunction;
import org.qcri.hackit.core.tagger.HackItTagger;
import org.qcri.hackit.core.tuple.HackItTuple;

public class MapHackitTagger<I, O> extends HackItTagger implements MapFunction<HackItTuple<Long, I>, HackItTuple<Long, O>> {

    private MapFunction<I, O> function;

    public MapHackitTagger(MapFunction<I, O> function) {
        this.function = function;
    }

    @Override
    public HackItTuple<Long, O> map(HackItTuple<Long, I> value) throws Exception {
        this.preTaggingTuple(value);
        O result = this.function.map(value.getValue());
        return this.postOneToOneTuple(value.getHeader().getId(), result);
    }
}