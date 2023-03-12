package org.qcri.hackit.core.tagger.template.system;

public interface FunctionTemplateSystem<I, O> {

    public O execute(I input);
}
