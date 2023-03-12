package org.qcri.hackit.core.tagger.template;

import org.qcri.hackit.core.tagger.HackItTagger;
import org.qcri.hackit.core.tuple.HackItTuple;
import org.qcri.hackit.core.tagger.template.system.FunctionTemplateSystem;

public class PredicateTemplateHackItTagger<K, I> extends HackItTagger implements FunctionTemplateSystem<HackItTuple<K, I>, Boolean> {

    private FunctionTemplateSystem<I, Boolean> function;

    public PredicateTemplateHackItTagger(FunctionTemplateSystem<I, Boolean> function) {
        this.function = function;
    }

    @Override
    public Boolean execute(HackItTuple<K, I> v1) {
        this.preTaggingTuple(v1);
        Boolean result = this.function.execute(v1.getValue());
        this.postTaggingTuple(v1);
        return result;
    }
}
