package org.qcri.hackit.core.iterators;

import java.io.Serializable;
import java.util.Iterator;
import java.util.function.Function;

public class FunctionIterator<I, O> implements Iterator<O>, Serializable {

    private Iterator<I> base;
    private Function<I, O> function;

    public FunctionIterator(Iterator<I> base, Function<I, O> function) {
        this.base = base;
        this.function = function;
    }

    @Override
    public boolean hasNext() {
        return this.base.hasNext();
    }

    @Override
    public O next() {
        return this.function.apply(this.base.next());
    }
}
