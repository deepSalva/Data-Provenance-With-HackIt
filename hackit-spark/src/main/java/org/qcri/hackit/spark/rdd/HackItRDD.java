package org.qcri.hackit.spark.rdd;

import org.qcri.hackit.core.sniffer.clone.DefaultCloner;
import org.qcri.hackit.core.sniffer.inject.EmptyHackItInjector;
import org.qcri.hackit.shipper.rabbitmq.receiver.ReceiverMultiChannelRabbitMQ;
import org.qcri.hackit.shipper.rabbitmq.sender.SenderMultiChannelRabbitMQ;
import org.qcri.hackit.spark.sniffer.HackItSnifferSpark;
import org.qcri.hackit.spark.tagger.FlatMapFunctionHackItTagger;
import org.qcri.hackit.spark.tagger.FunctionHackItTagger;
import org.qcri.hackit.spark.tagger.PairFunctionHackItTagger;
import org.qcri.hackit.spark.tagger.PredicateHackItTagger;
import org.qcri.hackit.core.tags.LogTag;
import org.qcri.hackit.core.tuple.HackItTuple;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.rdd.RDD;


public class HackItRDD<K, T> {
    private JavaRDD<HackItTuple<K, T>> rdd;

    private HackItRDD(JavaRDD<HackItTuple<K, T>> rdd) {
        this.rdd = rdd;
    }

    /**
     * Wrap the JavaRDD inside of DebugRDD
     * */
    public static <K, T> HackItRDD<K, T> fromJavaRDD(JavaRDD<T> rdd){
        HackItRDD<K, T> ktHackItRDD = new HackItRDD<K, T>(
                rdd.map((Function<T, HackItTuple<K, T>>) HackItTuple::new)
        );
        return ktHackItRDD;
    }

    /**
     * unwrap the RDD and coming back to the normal execution
     * */
    public RDD<T> toRDD(){
        return this.toJavaRDD().rdd();
    }

    /**
     * unwrap the RDD and coming back to the normal execution
     * */
    public JavaRDD<T> toJavaRDD(){
        return this.rdd.map(hackit -> hackit.getValue());
    }

    public static <K, T> HackItRDD<K, T> wrapDebugRDD(JavaRDD<T> rdd){
        return HackItRDD.fromJavaRDD(rdd);
    }
    // Common RDD functions


    public HackItRDD<K, T> filter(Function<T, Boolean> f) {
        HackItRDD<K, T> ktHackItRDD = new HackItRDD<>(
            this.rdd.filter(
                    (Function<HackItTuple<K, T>, Boolean>) new PredicateHackItTagger<K, T>(f).addPostTag(new LogTag())
            )
        );
        return ktHackItRDD;
    }

    public <O> HackItRDD<K, O> map(Function<T, O> f){
        HackItRDD<K, O> ktHackItRDD = new HackItRDD<>(
            this.rdd.map(
                new FunctionHackItTagger<>(f)
            )
        );
        return ktHackItRDD;
    }

    public <KO, O> HackItPairRDD<K, KO, O> mapToPair(PairFunction<T, KO, O> pairFunction){
        HackItPairRDD<K, KO, O> ktDebugRDD = new HackItPairRDD<>(
            this.rdd.mapToPair(
                new PairFunctionHackItTagger<>(pairFunction)
            )
        );
        return ktDebugRDD;
    }

    public <KO, O> HackItRDD<K, O> flatMap(FlatMapFunction<T, O> flatMapFunction){
        HackItRDD<K, O> koHackItRDD = new HackItRDD<>(
            this.rdd.flatMap(
                new FlatMapFunctionHackItTagger<K, T, O>(
                    flatMapFunction
                )
            )
        );
        return koHackItRDD;
    }

    /*
    public HackItRDD<K, T> sniffer(){
        HackItSnifferSpark<Long, String, byte[], SenderMultiChannelRabbitMQ<byte[]>, ReceiverMultiChannelRabbitMQ<Long, String>> lala = new HackItSnifferSpark<Long, String, byte[], SenderMultiChannelRabbitMQ<byte[]>, ReceiverMultiChannelRabbitMQ<Long, String>>(
                new EmptyHackItInjector<>(),
                ele -> true,
                new HackItShipperDirectRabbitMQ(),
                ele -> true, // -> every element will be 'sniffed'
                new DefaultCloner<HackItTuple<Long, String>>()
        );
        return this.sniffer(lala);
    }

     */

    public HackItRDD<K, T> sniffer(HackItSnifferSpark hackItSniffer){
        return new HackItRDD<>(
           this.rdd.flatMap(
               hackItSniffer
           )
        );
    }


    /*@Override
    public <R> DebugRDD<R> map(Function<T, R> f) {
        return null;
    }

    @Override
    public <K2, V2> DebugRDD<K2, V2> mapToPair(PairFunction<T, K2, V2> f) {
        return null;
    }

    @Override
    public <U> DebugRDD<U> flatMap(FlatMapFunction<T, U> f) {
        return null;
    }

    @Override
    public <K2, V2> DebugRDD<K2, V2> flatMapToPair(PairFlatMapFunction<T, K2, V2> f) {
        return null;
    }
    */

    public void saveAsTextFile(String path) {
        this.toJavaRDD().saveAsTextFile(path);
    }
}
