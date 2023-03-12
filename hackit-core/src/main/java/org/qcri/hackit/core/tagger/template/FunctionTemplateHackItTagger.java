package org.qcri.hackit.core.tagger.template;

import org.qcri.hackit.core.tagger.HackItTagger;
import org.qcri.hackit.core.tagger.template.system.FunctionTemplateSystem;
import org.qcri.hackit.core.tuple.HackItTuple;

public class FunctionTemplateHackItTagger<K, I, O> extends HackItTagger implements FunctionTemplateSystem<HackItTuple<K, I>, HackItTuple<K, O>> {

    private FunctionTemplateSystem<I, O> function;

    public FunctionTemplateHackItTagger(FunctionTemplateSystem<I, O> function) {
        this.function = function;
    }

    @Override
    public HackItTuple<K, O> execute(HackItTuple<K, I> v1) {
        this.preTaggingTuple(v1);
        O result = this.function.execute(v1.getValue());
        return this.postTaggingTuple(v1, result);
    }
}
