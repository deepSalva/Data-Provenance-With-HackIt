package org.qcri.hackit.flink;

import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.util.Collector;
import org.qcri.hackit.core.tagger.HackItTagger;
import org.qcri.hackit.core.tuple.HackItTuple;
import org.qcri.hackit.flink.HackItDataset.HackItDataset;


/**
 * Test class which creates Flink Execution Environment and TPCHQuery3 with HackItOperators
 */
public class FlinkHackIt {

    public static void main(String[] args){
        String input_path = "/Users/pilartorres/Documents/SALVA-JAVA/JAVA_PROJECTS/HackIt/hackit-src/hackit-experiments/dataset/SQL_server.txt";// => args[2]
        String output_path = "/Users/pilartorres/Documents/SALVA-JAVA/JAVA_PROJECTS/HackIt/hackit-src/hackit-experiments/dataset";// => args[3]
        String grep_condition = "Flink";// => args[4]


        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(4);





        // Test Filter
        DataSet<String> lines = env.readTextFile(input_path);
        HackItDataset hackItDataset = HackItDataset.fromDataSet(lines).filter(new FilterFunction<String>() {
            @Override
            public boolean filter(String value) throws Exception {
                return value.contains("Flink");
            }
        });

        hackItDataset.toDataSet().writeAsText(output_path+ "/filter", FileSystem.WriteMode.OVERWRITE).setParallelism(1);


        // Test Map
        DataSet<Integer> numbers = env.fromElements(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        HackItDataset hackItMapped = HackItDataset.fromDataSet(numbers).map(new MapFunction<Integer, Integer>() {
            @Override
            public Integer map(Integer value) throws Exception {
                return value * 2;
            }
        }).sniffer( // sniff on every incoming tuple
                ele -> true,
                ele -> true
        );

        hackItMapped.toDataSet().writeAsText(output_path+"/map", FileSystem.WriteMode.OVERWRITE).setParallelism(1);

        // Test Reduce
        DataSet<Integer> reduce_nums = env.fromElements(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        HackItDataset<Integer> reduced = HackItDataset.fromDataSet(reduce_nums).reduce(new ReduceFunction<Integer>() {
            @Override
            public Integer reduce(Integer value1, Integer value2) throws Exception {
                return value1 + value2;
            }
        });

        reduced.toDataSet().writeAsText(output_path+"/reduce", FileSystem.WriteMode.OVERWRITE).setParallelism(1);


        // Test new FlatMap directly
        DataSet<String> line = env.readTextFile(input_path);
        HackItDataset<String> flatMap = HackItDataset.fromDataSet(line).flatMap(new HackItFlatMapTagger<>());

        flatMap.toDataSet().writeAsText(output_path+"/flatMap", FileSystem.WriteMode.OVERWRITE).setParallelism(1);

        try {
            env.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


     /**
     * Simple FlatMap Function Implementation which also works as the HackItTagger.
     * For every incoming line (String) it outputs the whitespace separated WORDS
     */
    public static class HackItFlatMapTagger<Long> extends HackItTagger implements FlatMapFunction<HackItTuple<Long, String>, HackItTuple<Long, String>> {
        @Override
        public void flatMap(HackItTuple<Long, String> value, Collector<HackItTuple<Long, String>> out) throws Exception {
            this.preTaggingTuple(value);
            // normalize and split the line
            String[] tokens = value.getValue().toLowerCase().split("\\W+");

            // emit the pairs
            for (String token : tokens) {
                HackItTuple hackItTuple = new HackItTuple<>(value.getHeader(), token);
                this.postTaggingTuple(hackItTuple);
                out.collect(hackItTuple);
            }
        }
    }

}
