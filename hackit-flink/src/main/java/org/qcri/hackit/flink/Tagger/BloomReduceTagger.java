package org.qcri.hackit.flink.Tagger;



import com.google.common.primitives.Longs;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.GroupReduceFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;
import org.apache.hadoop.util.bloom.BloomFilter;
import org.apache.hadoop.util.bloom.Key;
import org.apache.hadoop.util.hash.MurmurHash;
import org.qcri.hackit.core.Lineage.BloomToOneLineage;
import org.qcri.hackit.core.tagger.HackItTagger;
import org.qcri.hackit.core.tags.LineageTag;
import org.qcri.hackit.core.tuple.HackItTuple;
import org.qcri.hackit.core.tuple.HackItTupleHeaderLong;

import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;

public class BloomReduceTagger<T> extends HackItTagger implements GroupReduceFunction<HackItTuple<Long, T>, Tuple2<HackItTuple<Long, T>, BloomToOneLineage>>, Serializable {

    private ReduceFunction<T> reduceFunction;
    private int vectorsize;

    public BloomReduceTagger(ReduceFunction<T> reduceFunction, int vectorsize, FilterFunction<HackItTuple<Long, T>> lineageCondition) throws IOException {
        super(lineageCondition);
        this.reduceFunction = reduceFunction;
        this.vectorsize = vectorsize;
    }

    public void reduce(Iterable<HackItTuple<Long, T>> values, Collector<Tuple2<HackItTuple<Long, T>, BloomToOneLineage>> out) throws Exception {
        int nbHash = 5;
        int hashType = MurmurHash.MURMUR_HASH;
        BloomFilter bloomFilter = new BloomFilter(vectorsize, nbHash, hashType);

        Iterator<HackItTuple<Long, T>> iterator = values.iterator();
        HackItTuple<Long, T> tuple = iterator.next();
        T result = tuple.getValue();
        bloomFilter.add(new Key(Longs.toByteArray(tuple.getHeader().getId())));
        while (iterator.hasNext()){
            tuple = iterator.next();
            bloomFilter.add(new Key(Longs.toByteArray(tuple.getHeader().getId())));

            result = reduceFunction.reduce(tuple.getValue(), result);
        }

        // Following code creates the hackit-tuple and saves data to file
        HackItTuple<Long, T> output = new HackItTuple<>(new HackItTupleHeaderLong(),result);
        this.postTaggingTuple(output);

        BloomToOneLineage lineage = new BloomToOneLineage(bloomFilter, output.getHeader().getId());

        if (this.lineageCondition != null){
            FilterFunction<HackItTuple<Long, T>> condition = this.lineageCondition;

            if (condition.filter(output)){
                out.collect(new Tuple2<>(output, lineage));
            }
        }

        out.collect(new Tuple2<>(output, null));
    }
}


