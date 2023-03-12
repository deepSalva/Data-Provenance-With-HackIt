package org.qcri.hackit.core.tagger.template;

import org.qcri.hackit.core.tuple.HackItTuple;
import org.qcri.hackit.core.tagger.HackItTagger;
import org.qcri.hackit.core.tagger.template.system.FlatMapTemplateSystem;

import java.util.Iterator;

public class FlatMapTemplateHackItTagger<K, I, O>
        extends HackItTagger
        implements FlatMapTemplateSystem<HackItTuple<K, I>, HackItTuple<K, O>> {

    private FlatMapTemplateSystem<I, O> function;

    public FlatMapTemplateHackItTagger(
            FlatMapTemplateSystem<I, O> function
    ) {
        this.function = function;
    }


    @Override
    public Iterator<HackItTuple<K, O>> execute(HackItTuple<K, I> kiHackItTuple) {
        this.preTaggingTuple(kiHackItTuple);
        Iterator<O> result = this.function.execute(kiHackItTuple.getValue());
        return this.postTaggingTuple(kiHackItTuple, result);
    }
}
