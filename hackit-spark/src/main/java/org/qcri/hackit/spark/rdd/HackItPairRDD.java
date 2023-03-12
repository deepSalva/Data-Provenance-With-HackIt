package org.qcri.hackit.spark.rdd;

import org.qcri.hackit.core.tuple.HackItTuple;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.function.Function2;
import scala.Tuple2;

public class HackItPairRDD<K, KT, V>{
    private JavaPairRDD<KT, HackItTuple<K, V>> rdd;

    public HackItPairRDD(JavaPairRDD<KT, HackItTuple<K, V>> rdd) {
        this.rdd = rdd;
    }

    public HackItPairRDD<K, KT, V> reduceByKey(Function2<V, V, V> func) {
        return new HackItPairRDD<>(
            this.rdd
                .mapValues(
                    HackItTuple::getValue
                )
                .reduceByKey(
                    func
                )
                .mapValues(
                    HackItTuple::new
                )
        );
    }

    public HackItPairRDD<K, KT, V> join(HackItPairRDD<K, KT, V> other){
        JavaPairRDD<KT, Tuple2<HackItTuple<K, V>, HackItTuple<K, V>>> tmp = this.rdd.join(other.rdd);
        return null;
    }


/*
    /**
     * Wrap the RDD inside of DebugRDD
     * *
    public static <T> DebugPairRDD<T> fromRDD(RDD<T> rdd){
        return this(rdd, rdd.elementClassTag());
    }

    /**
     * Wrap the JavaRDD inside of DebugRDD
     * *
    public static <T> DebugPairRDD<T> fromJavaRDD(JavaRDD<T> rdd){
        return new DebugRDD<T>(rdd.rdd(), rdd.classTag());
    }

    /**
     * unwrap the RDD and coming back to the normal execution
     * *
    public RDD<Tuple2<K, V>> toRDD(){
        return this.toPairJavaRDD().rdd();
    }

    /**
     * unwrap the RDD and coming back to the normal execution
     * *
    public JavaPairRDD<K, V> toPairJavaRDD(){
        RDD<Tuple2<K, V>> tmp = this.rdd();
        if(this.classTag().getClass() == HackItTuple.class) {
            tmp = null;//((JavaRDD<HackItTuple<T>>)this.rdd).map(com.qcri.hackit -> com.qcri.hackit.getValue());
        }
        return super.fromRDD(tmp, this.kClassTag(), this.vClassTag());
    }

    public static <T> DebugRDD<T> wrapDebugRDD(JavaRDD<T> rdd){
        return DebugRDD.fromJavaRDD(rdd);
    }


    @Override
    public Map<K, V> reduceByKeyLocally(Function2<V, V, V> func) {
        return super.reduceByKeyLocally(func);
    }

    @Override
    public <W> JavaPairRDD<K, Tuple2<V, W>> join(JavaPairRDD<K, W> other) {
        return super.join(other);
    }

    @Override
    public void saveAsTextFile(String path) {
        super.saveAsTextFile(path);
    }*/
}
