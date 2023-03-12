package org.qcri.hackit.core.wrapper;

import org.apache.spark.api.java.function.Function;

public class HackItPredicate<T> extends HackItTagger<T, Boolean> implements Function<T, Boolean> {

    final private Function<T, Boolean> predicate;
    final private Class<T> tClass;

    public HackItPredicate(Function<T, Boolean> predicate, Class<T> tClass) {
        this.predicate = predicate;
        this.tClass = tClass;
    }


    @Override
    public Boolean call(T v1) throws Exception {
        return null;
    }

    @Override
    public Boolean apply(T input) {
        return null;
    }
}
