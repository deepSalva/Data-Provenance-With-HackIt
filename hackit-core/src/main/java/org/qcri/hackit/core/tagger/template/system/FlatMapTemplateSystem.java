package org.qcri.hackit.core.tagger.template.system;

import java.util.Iterator;

public interface FlatMapTemplateSystem<I, O> {

    public Iterator<O> execute(I input);
}
