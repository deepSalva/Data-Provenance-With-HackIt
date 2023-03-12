package org.qcri.hackit.core.tagger;

import com.google.common.hash.BloomFilter;
import org.apache.flink.api.common.functions.FilterFunction;
import org.qcri.hackit.core.iterators.HackItIterator;
import org.qcri.hackit.core.tags.HackItTag;
import org.qcri.hackit.core.tags.LineageTag;
import org.qcri.hackit.core.tuple.HackItTuple;
import org.qcri.hackit.core.tuple.HackItTupleHeader;
import org.qcri.hackit.core.tuple.HackItTupleHeaderLong;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


public class HackItTagger<O> implements Serializable {

    protected List<HackItTag> pre_tags;
    protected List<HackItTag> post_tags;
    private final static String TOPIC = "lineageTest1";
    private final static String BOOTSTRAP_SERVERS = "localhost:9092,localhost:9093,localhost:9094";
    protected final FilterFunction lineageCondition;

    public HackItTagger(){
        this.lineageCondition = null;
    }

    public HackItTagger(FilterFunction<HackItTuple<Long, O>> lineageCondition) {
        this.lineageCondition = lineageCondition;
    }

    public HackItTagger addPreTag(HackItTag tag){
        if(this.pre_tags == null){
            this.pre_tags = new ArrayList<>();
        }
        this.pre_tags.add(tag);
        return this;
    }

    public HackItTagger addPostTag(HackItTag tag){
        if(this.post_tags == null){
            this.post_tags = new ArrayList<>();
        }
        this.post_tags.add(tag);
        return this;
    }

    public void preTaggingTuple(HackItTuple tuple){
        if(this.pre_tags != null)
            taggingTuple(tuple, this.pre_tags);
    }

    public void postTaggingTuple(HackItTuple tuple){
        if(this.post_tags != null)
            taggingTuple(tuple, this.post_tags);
    }

    public void taggingTuple(HackItTuple tuple, List<HackItTag> tags){
        tags.stream().forEach(tag -> tuple.addTag(tag.getInstance()));
    }

    /**
     * code for spark so project compiles
     */
    public <K, I> HackItTuple<K, O> postTaggingTuple(HackItTuple<K, I> hackItInput, O output){
        HackItTuple<K, O> koHackItTuple = new HackItTuple<>(output);
        postTaggingTuple(koHackItTuple);
        return koHackItTuple;
    }

    /**
     * code for spark so project compiles
     */
    public <K, I> Iterator<HackItTuple<K,O>> postTaggingTuple(HackItTuple<K, I> origin, Iterator<O>result){
        HackItTupleHeader<K> header = origin.getHeader();
        Iterator<HackItTuple<K, O>> iter_result = new HackItIterator<K, O>(
                result,
                record -> {
                    HackItTuple<K, O> tuple = new HackItTuple<K, O>(
                            header.createChild(),
                            record
                    );
                    postTaggingTuple(tuple);
                    return tuple;
                }
        );
        return iter_result;
    }




    public void sendoutLineage(BloomFilter input, long outputId){
    }

    public void sendoutLineage(LinkedList<Long> input, long outputId){
        input.addLast(outputId);

        KafkaObjectProducer producer = new KafkaObjectProducer();
        try {
            producer.produceToKafka(input);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendoutLineage(long inputId, LinkedList<Long> output) throws Exception {
        KafkaProducerOne<Long, LinkedList> producerOne = new KafkaProducerOne<Long, LinkedList>();
        producerOne.produceToKafka(inputId, output);
        }

    public void sendoutLineage(List<Long> input, List<Long> output){

    }



    public HackItTuple postOneToOneTuple(long id, O output) throws Exception {
        HackItTuple<Long, O> hackItTuple = new HackItTuple<Long, O>(new HackItTupleHeaderLong(id), output);
        postTaggingTuple(hackItTuple);
        return hackItTuple;
    }



    public HackItTuple<Long, O> postReduceTuple(LinkedList<Long> inputSetList, O reduceOutput) throws Exception {
        HackItTuple<Long, O> hackItTupleOutput = new HackItTuple<>(new HackItTupleHeaderLong(),reduceOutput);

        if (lineageCondition.filter(hackItTupleOutput)) {
            sendoutLineage(inputSetList, hackItTupleOutput.getHeader().getId());
            hackItTupleOutput.addTag(new LineageTag());
        }

        postTaggingTuple(hackItTupleOutput);
        return hackItTupleOutput;
    }
}
