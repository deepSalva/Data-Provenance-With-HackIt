package org.qcri.hackit.flink.Tagger;

import org.apache.flink.api.common.functions.FilterFunction;
import org.qcri.hackit.core.tagger.HackItTagger;
import org.qcri.hackit.core.tuple.HackItTuple;

public class FilterHackItTagger<I> extends HackItTagger implements FilterFunction<HackItTuple<Long, I>> {

    private FilterFunction<I> filterFunction;

    public FilterHackItTagger(FilterFunction<I> filterFunction) {
        this.filterFunction = filterFunction;
    }

    @Override
    public boolean filter(HackItTuple<Long, I> input) throws Exception {
        this.preTaggingTuple(input);
        Boolean result = this.filterFunction.filter(input.getValue());
        this.postOneToOneTuple(input.getHeader().getId(), input); // This actually transforms a tuple -- might check whether the tags stick...
        return result;
    }
}
