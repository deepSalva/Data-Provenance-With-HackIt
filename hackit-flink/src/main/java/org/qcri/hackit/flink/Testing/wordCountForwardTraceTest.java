package org.qcri.hackit.flink.Testing;


import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.operators.MapOperator;
import org.apache.flink.api.java.operators.ReduceOperator;
import org.apache.flink.api.java.operators.UnsortedGrouping;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.util.Collector;
import org.qcri.hackit.core.tagger.HackItTagger;
import org.qcri.hackit.core.tuple.HackItTuple;
import org.qcri.hackit.core.tuple.HackItTupleHeaderLong;
import org.qcri.hackit.flink.HackItDataset.HackItDataset;




import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class wordCountForwardTraceTest {

    public static JobExecutionResult runWithHackit(String inputPath, String outputPath, boolean useBloom) throws Exception {

        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(4);



        DataSet<String> lines = env.readTextFile(inputPath);

        HackItDataset<String> hackItDataset = HackItDataset.fromDataSet(lines);

        // Split a line into its words using a FlatMap function
        HackItDataset<String> words = hackItDataset.flatMap(new LinesToWordFlatMap(new FilterFunction<HackItTuple<Long, String>>(){
            @Override
            public boolean filter(HackItTuple<Long, String> value) throws Exception {
                return ( value.getValue().length() > 0 && (value.getValue().substring(0, 1).matches("[a-m]"))); // TODO: this is an just an example for conditional lineage capturing
            }
        }));

        // Map the words to (Word, 1) to enable reduce later on
        HackItDataset<Tuple2<String, Integer>> preCount = words.map(new MapFunction<String, Tuple2<String, Integer>>() {
            @Override
            public Tuple2<String, Integer> map(String value) throws Exception {
                return new Tuple2<>(value, 1);
            }
        });

        // group the Dataset on each Word -> each word is in its own group
        HackItDataset<Tuple2<String, Integer>> groupedByWord = preCount.groupBy(new KeySelector<HackItTuple<Long, Tuple2<String, Integer>>, String>() {
            @Override
            public String getKey(HackItTuple<Long, Tuple2<String, Integer>> value) throws Exception {
                return value.getValue().f0;
            }
        });

        HackItDataset<Tuple2<String, Integer>> wordCount = null;

        if (useBloom) {
            wordCount = groupedByWord.reduceWithBloomFilter(new ReduceFunction<Tuple2<String, Integer>>() {
                @Override
                public Tuple2<String, Integer> reduce(Tuple2<String, Integer> value1, Tuple2<String, Integer> value2) throws Exception {
                    return new Tuple2<>(value1.f0, value1.f1 + value2.f1);
                }
            }, 10000, new FilterFunction<HackItTuple<Long, Tuple2<String, Integer>>>(){
                @Override
                public boolean filter(HackItTuple<Long, Tuple2<String, Integer>> value) throws Exception {
                    return value.getValue().f1 > 3; // TODO: this is just an example for conditional lineage capturing
                }
            });
        }else {
            // Reduce on the grouped Dataset to compute the count of each word
            wordCount = groupedByWord.reduce(new ReduceFunction<Tuple2<String, Integer>>() {
                @Override
                public Tuple2<String, Integer> reduce(Tuple2<String, Integer> value1, Tuple2<String, Integer> value2) throws Exception {
                    return new Tuple2<>(value1.f0, value1.f1 + value2.f1);
                }
            });
        }

        wordCount.toDataSet().writeAsText(outputPath, FileSystem.WriteMode.OVERWRITE).setParallelism(1);

        try {
            JobExecutionResult result = env.execute("WordCount Example");
            result.getNetRuntime(TimeUnit.MILLISECONDS);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JobExecutionResult runWordCountNormal(String input, String output){
        JobExecutionResult result = null;

        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();

        DataSet<String> lines = env.readTextFile(input);

        // Split a line into its words using a FlatMap function
        DataSet<String> words = lines.flatMap(new FlatMapFunction<String, String>() {
            @Override
            public void flatMap(String value, Collector<String> out) throws Exception {
                String[] words = value.split(" ");
                for (String w: words) {
                    out.collect(w);
                }
            }
        });

        // Map the words to (Word, 1) to enable reduce later on
        MapOperator<String, Tuple2<String, Integer>> preCount = words.map(new MapFunction<String, Tuple2<String, Integer>>() {
            @Override
            public Tuple2<String, Integer> map(String value) throws Exception {
                return new Tuple2<>(value, 1);
            }
        });

        // group the Dataset on each Word -> each word is in its own group
        UnsortedGrouping<Tuple2<String, Integer>> groupedByWord = preCount.groupBy(0);


        // Reduce on the grouped Dataset to compute the count of each word
        ReduceOperator<Tuple2<String, Integer>> wordWithCount = groupedByWord.reduce(new ReduceFunction<Tuple2<String, Integer>>() {
            @Override
            public Tuple2<String, Integer> reduce(Tuple2<String, Integer> value1, Tuple2<String, Integer> value2) throws Exception {
                return new Tuple2<>(value1.f0, value1.f1 + value2.f1);
            }
        });


        wordWithCount.writeAsText(output + "wordCountNormal.txt", FileSystem.WriteMode.OVERWRITE).setParallelism(1);

        try {
            result = env.execute("WordCount Example");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }



    /**
     * FlatMap HackItTagger implementation which sends out the operator lineage information once for each input!
     * (instead of once for each output)
     */
    public static class LinesToWordFlatMap extends HackItTagger implements FlatMapFunction<HackItTuple<Long, String>, HackItTuple<Long, String>> {
        public LinesToWordFlatMap(FilterFunction lineageCondition) {
            super(lineageCondition);
        }

        @Override
        public void flatMap(HackItTuple<Long, String> inputTuple, Collector<HackItTuple<Long, String>> out) throws Exception {
            this.preTaggingTuple(inputTuple);
            String[] words = inputTuple.getValue().split(" ");
            LinkedList<Long> linOut = new LinkedList<>();
            for (int i = 0; i < words.length; i++) {
                String word = words[i].trim();
                if (word.length() > 0){
                    HackItTuple<Long, String> outputTuple = new HackItTuple(new HackItTupleHeaderLong(), word);
                    this.postTaggingTuple(outputTuple);
                    if (this.lineageCondition.filter(outputTuple)){ // only send out lineage tuple based on the lineage capturing condition
                        linOut.add(outputTuple.getHeader().getId());
                    } else {
                        System.out.println("Output ID: " + outputTuple.getHeader().getId() + " not captured due to failed condition: " + outputTuple.getValue() + " does not begin with [a-m]");
                    }
                    out.collect(outputTuple);
                }
            }
            this.sendoutLineage(inputTuple.getHeader().getId(), linOut);
        }
    }
}
