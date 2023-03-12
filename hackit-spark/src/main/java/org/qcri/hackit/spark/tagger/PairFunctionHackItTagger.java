package org.qcri.hackit.spark.tagger;

import org.qcri.hackit.core.tuple.HackItTuple;
import org.qcri.hackit.core.tagger.HackItTagger;
import org.apache.spark.api.java.function.PairFunction;
import scala.Tuple2;

public class PairFunctionHackItTagger<KeyTuple, KeyPair, InputType, OutputType> extends HackItTagger
        implements PairFunction<HackItTuple<KeyTuple, InputType>, KeyPair, HackItTuple<KeyTuple, OutputType>> {

    private PairFunction<InputType, KeyPair, OutputType> function;

    public PairFunctionHackItTagger(PairFunction<InputType, KeyPair, OutputType> function) {
        this.function = function;
    }

    @Override
    public Tuple2<KeyPair, HackItTuple<KeyTuple, OutputType>> call(HackItTuple<KeyTuple, InputType> v1) throws Exception {
        this.preTaggingTuple(v1);
        Tuple2<KeyPair, OutputType> result = this.function.call(v1.getValue());
        HackItTuple<KeyTuple, OutputType> hackItTuple_result = new HackItTuple<KeyTuple, OutputType>(v1.getHeader(), result._2());
        this.postTaggingTuple(hackItTuple_result);
        return new Tuple2<>(result._1(), hackItTuple_result);
    }
}
