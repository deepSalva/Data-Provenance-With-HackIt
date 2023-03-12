package org.qcri.hackit.core.iterators;

import org.qcri.hackit.core.tuple.HackItTuple;

import java.util.Iterator;
import java.util.function.Function;

public class HackItIterator<K, T> extends FunctionIterator<T, HackItTuple<K, T>>{

    public HackItIterator(Iterator<T> base, Function<T, HackItTuple<K, T>> function) {
        super(base, function);
    }

}
