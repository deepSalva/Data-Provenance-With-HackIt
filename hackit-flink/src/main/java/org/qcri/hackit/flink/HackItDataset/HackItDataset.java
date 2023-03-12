package org.qcri.hackit.flink.HackItDataset;

import org.apache.flink.api.common.functions.*;
import org.apache.flink.api.common.io.FileOutputFormat;
import org.apache.flink.api.common.io.SerializedOutputFormat;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.operators.GroupReduceOperator;
import org.apache.flink.api.java.operators.MapOperator;
import org.apache.flink.api.java.operators.ProjectOperator;
import org.apache.flink.api.java.operators.UnsortedGrouping;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple1;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.util.Collector;
import org.qcri.hackit.core.Lineage.BloomToOneLineage;
import org.qcri.hackit.core.sniffer.actor.HackItActor;
import org.qcri.hackit.core.sniffer.clone.DefaultCloner;
import org.qcri.hackit.core.sniffer.inject.EmptyHackItInjector;
import org.qcri.hackit.core.sniffer.sniff.HackItSniff;
import org.qcri.hackit.core.tagger.HackItTagger;
import org.qcri.hackit.core.tags.LineageTag;
import org.qcri.hackit.core.tags.LogTag;
import org.qcri.hackit.core.tuple.HackItTuple;
import org.qcri.hackit.flink.Sniffer.HackItSnifferFlink;
import org.qcri.hackit.flink.Tagger.BloomReduceTagger;
import org.qcri.hackit.flink.Tagger.FilterHackItTagger;
import org.qcri.hackit.flink.Tagger.MapHackitTagger;
import org.qcri.hackit.flink.Tagger.MappingReduceTagger;
import org.qcri.hackit.shipper.rabbitmq.HackItShipperDirectRabbitMQ;
import org.qcri.hackit.shipper.rabbitmq.receiver.ReceiverMultiChannelRabbitMQ;
import org.qcri.hackit.shipper.rabbitmq.sender.SenderMultiChannelRabbitMQ;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.function.Function;

/**
 * Implementation of a Wrapper Class for Flinks Dataset - implicitly specifies Long as HackItHeader IDs
 * @param <T>   Type of Value of the HackItTuple
 */
public class HackItDataset<T> {
    private DataSet<HackItTuple<Long, T>> dataSet; // K = Header, T = Value / original Tuple
    private UnsortedGrouping<HackItTuple<Long, T>> dataGroup;


    public DataSet<HackItTuple<Long, T>> getDataSet() {
        return dataSet;
    }

    public HackItDataset(DataSet<HackItTuple<Long, T>> dataSet) {
        this.dataSet = dataSet;
    }

    public HackItDataset(UnsortedGrouping<HackItTuple<Long, T>> dataGroup) {
        this.dataGroup = dataGroup;
    }

    /**
     * Wrap the DataSet inside of HackitWrapper
     * */
    public static <T> HackItDataset<T> fromDataSet(DataSet<T> dataSet){

        HackItDataset<T> ktHackItDataset = new HackItDataset<T>(
                dataSet.map((MapFunction<T, HackItTuple<Long, T>>) HackItTuple::new).returns(new TypeHint<HackItTuple<Long, T>>() {
                    @Override
                    public TypeInformation<HackItTuple<Long, T>> getTypeInfo() {
                        return super.getTypeInfo();
                    }
                })
        );
        return ktHackItDataset;
    }


    /**
     * unwrap and come back to the normal execution
     * */
    public DataSet<T> toDataSet(){
        return this.dataSet.map(hackItTuple -> hackItTuple.getValue());
    }


    public <O> HackItDataset<O> map (MapFunction<T, O> f){
        HackItDataset<O> ktHackItDataSet = new HackItDataset<>(
                this.dataSet.map(
                        (MapFunction<HackItTuple<Long,T>, HackItTuple<Long,O>>) new MapHackitTagger<>(f).addPostTag(new LogTag()) // .addPreTag(new LogTag()) // can add all kinds of different tags here...
                )
        );
        return ktHackItDataSet;
    }

    public HackItDataset<T> filter(FilterFunction<T> filterFunction){
        HackItDataset<T> ktHackItDataset = new HackItDataset<>(
            this.dataSet.filter(
                    (FilterFunction<HackItTuple<Long,T>>) new FilterHackItTagger<T>(filterFunction) // .addPostTag(new LogTag())
            )
        );
        return ktHackItDataset;
    }


    /**
     * Method which applies a user provided reduce function to this dataset. Additionally the lineage information in form of a BloomFilter -> OutputID of this operator will be saved to a file
     * when a user-provided condition is met.
     *
     * @param reduceFunction
     * @param vectorsize
     * @param lineageCondition
     * @return
     * @throws IOException
     */
    public HackItDataset<T> reduceWithBloomFilter(ReduceFunction<T> reduceFunction, int vectorsize, FilterFunction<HackItTuple<Long, T>> lineageCondition) throws IOException {

        if(this.dataGroup != null){
            GroupReduceOperator<HackItTuple<Long, T>, Tuple2<HackItTuple<Long, T>, BloomToOneLineage>> dataWithLineage = this.dataGroup.reduceGroup(
                    (GroupReduceFunction)new BloomReduceTagger<T>(reduceFunction, vectorsize, lineageCondition)
            );

            dataWithLineage.flatMap(new FlatMapFunction<Tuple2<HackItTuple<Long,T>, BloomToOneLineage>, BloomToOneLineage>() {
                @Override
                public void flatMap(Tuple2<HackItTuple<Long, T>, BloomToOneLineage> value, Collector<BloomToOneLineage> out) throws Exception {
                    if (value.f1 != null){
                        out.collect(value.f1);
                    }
                }
            }).write(new SerializedOutputFormat<BloomToOneLineage>(),"/Users/joschavonhein/Workspace/bdapro-ss20-dataprov/hackit-src/hackit-flink/Output/bloomfilter.ser", FileSystem.WriteMode.OVERWRITE)
            .setParallelism(1);

            MapOperator<Tuple2<HackItTuple<Long, T>, BloomToOneLineage>, HackItTuple<Long, T>> result = dataWithLineage.map(new MapFunction<Tuple2<HackItTuple<Long, T>, BloomToOneLineage>, HackItTuple<Long, T>>() {
                @Override
                public HackItTuple<Long, T> map(Tuple2<HackItTuple<Long, T>, BloomToOneLineage> value) throws Exception {
                    return value.f0;
                }
            });

            return new HackItDataset<T>(result);
        } else {
            GroupReduceOperator<HackItTuple<Long, T>, Tuple2<HackItTuple<Long, T>, BloomToOneLineage>> dataSetReduced = this.dataSet.reduceGroup(
                    (GroupReduceFunction)new BloomReduceTagger<T>(reduceFunction, vectorsize, lineageCondition)
            );

            dataSetReduced.map(new MapFunction<Tuple2<HackItTuple<Long,T>, BloomToOneLineage>, BloomToOneLineage>() {
                @Override
                public BloomToOneLineage map(Tuple2<HackItTuple<Long, T>, BloomToOneLineage> value) throws Exception {
                    return value.f1;
                }
            }).write(new SerializedOutputFormat<BloomToOneLineage>(),"/Users/joschavonhein/Workspace/bdapro-ss20-dataprov/hackit-src/hackit-flink/Output/bloomfilter.ser", FileSystem.WriteMode.OVERWRITE)
                    .setParallelism(1);

            return new HackItDataset<T>(
                dataSetReduced.map(new MapFunction<Tuple2<HackItTuple<Long,T>, BloomToOneLineage>, HackItTuple<Long, T>>() {
                @Override
                public HackItTuple<Long, T> map(Tuple2<HackItTuple<Long, T>, BloomToOneLineage> value) throws Exception {
                    return value.f0;
                }})
            );
        }
    }

    public HackItDataset<T> reduce(ReduceFunction<T> reduceFunction){

        if(this.dataGroup != null){
            GroupReduceOperator<HackItTuple<Long, T>, HackItTuple<Long, T>> groupReduced = this.dataGroup.reduceGroup(
                    new MappingReduceTagger<T>(reduceFunction)
            );
            return new HackItDataset<T>(groupReduced);
        }else {
            GroupReduceOperator<HackItTuple<Long, T>, HackItTuple<Long, T>> reducedResult = this.dataSet.reduceGroup(
                    new MappingReduceTagger<>(reduceFunction)
            );
            return new HackItDataset<>(reducedResult);
        }
    }


    public <K> HackItDataset<T> groupBy(KeySelector<HackItTuple<Long, T>, K> keySelector){
        return new HackItDataset<T>(this.dataSet.groupBy(keySelector));
    }

    /**
     * Function which allows the user to apply a flatMap transformation on the HackItDataset.
     * Different to the other methods the user has to implement
     *
     * @param <O>   Output Type
     * @param <F>   FlatMapFunction which also serves as a HackItTagger
     * @return
     */
    public <O, F extends HackItTagger & FlatMapFunction<HackItTuple<Long, T>, HackItTuple<Long, O>>> HackItDataset<O> flatMap(F flatMapFunction){

        HackItDataset<O> koHackItDataset = new HackItDataset<O>(
                this.dataSet.flatMap(flatMapFunction)
        );

        return koHackItDataset;
    }


    /**
     * Default Sniffer method which uses RabbitMQ as sender and receiver
     * Actor and Sniff functions still have to be provided!
     *
     * A Tuple is first inspected (sniffed) on it's header and only if that yields true will the actorFunction be called!
     *
     * @param actorFunction   the actor function which determines when a HackItTuple should be sent out based on its value
     * @param sniffFunction   the sniff function which determines when a HackItTuple should be sent out based on it's tags (Header)
     * @return
     */
    public HackItDataset<T> sniffer(HackItActor actorFunction, HackItSniff sniffFunction){

        HackItShipperDirectRabbitMQ<Long, T, byte[], SenderMultiChannelRabbitMQ<byte[]>, ReceiverMultiChannelRabbitMQ<Long, T>> shipper = new HackItShipperDirectRabbitMQ<>();

        HackItSnifferFlink<Long, T, byte[], SenderMultiChannelRabbitMQ<byte[]>, ReceiverMultiChannelRabbitMQ<Long, T>> hackItSnifferFlink = new HackItSnifferFlink<>(
                new EmptyHackItInjector<>(),
                actorFunction,
                shipper,
                sniffFunction,
                new DefaultCloner<HackItTuple<Long, T>>()
        );
        return this.sniffer(hackItSnifferFlink);
    }

    public HackItDataset<T> sniffer(HackItSnifferFlink hackItSnifferFlink){
        return new HackItDataset<>(this.dataSet.flatMap(hackItSnifferFlink));
    }


    /**
     * Helper Class which only pretags incoming HackItTuples and unwraps them.
     *
     * Problem: this instance of HackItTagger can't also be used to add PostTags -> what is the purpose of the pretags in this case?
     *
     * @param <K>
     * @param <I>
     */
    class PreHackItTagger<K, I> extends HackItTagger implements MapFunction<HackItTuple<K, I>, I> {

        @Override
        public I map(HackItTuple<K, I> value) throws Exception {
            this.preTaggingTuple(value);
            return value.getValue();
        }
    }

    /**
     * Helper Class which can tag outgoing tuples (for instance after a reduce operation)
     * @param <K>
     * @param <I>
     */
    class PostHackItTagger<K, I> extends HackItTagger implements MapFunction<I, HackItTuple<K, I>> {

        @Override
        public HackItTuple<K, I> map(I value) throws Exception {
            HackItTuple<K, I> tuple = new HackItTuple(value);
            this.postTaggingTuple(tuple);
            return tuple;
        }
    }

}
