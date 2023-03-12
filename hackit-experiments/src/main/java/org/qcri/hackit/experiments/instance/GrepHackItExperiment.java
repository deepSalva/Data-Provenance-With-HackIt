package org.qcri.hackit.experiments.instance;

import org.apache.spark.TaskContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.qcri.hackit.experiments.HackItExperiment;
import org.qcri.hackit.spark.rdd.HackItRDD;
import org.apache.spark.api.java.function.Function;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;

public class GrepHackItExperiment extends HackItExperiment implements Serializable {

    private String input_path;
    private String output_path;
    private String grep_condition;

    public GrepHackItExperiment(){
        super();

    }
    public GrepHackItExperiment(String name, String base_dir, String hostname, String input_path, String output_path, String grep_condition) {
        super(name, base_dir, hostname);
        this.input_path = input_path;
        this.output_path = output_path;
        this.grep_condition = grep_condition;
    }

    /**
     * Function which takes a line and returns true if the grep-condition is met
     * @return
     */
    private Function<String, Boolean> generateFunction(){
        final String condition = this.grep_condition;

        return new Function<String, Boolean>() {
            boolean firt_execution = true;
            TaskContext taskContext;

            public void init(){
                this.taskContext = TaskContext.get();
                this.firt_execution = false;
                System.out.println("here");
                System.out.println(this.taskContext.taskAttemptId());
            }

            @Override
            public Boolean call(String line) throws Exception {
                if( this.firt_execution ){
                    init();
                }
                //System.out.println(line);
                boolean result = line.contains(condition);
                /* if(result){
                    System.out.println(line);
                }*/
                return result;
            }
        };
    }

    /**
     * FlatMap Function which splits a line into its words (seperated by whitespace)
     * @return
     */
    private FlatMapFunction<String, String> lineToWordFunction(){

        return new FlatMapFunction<String, String>() {

            boolean first_execution = true;
            TaskContext taskContext;

            public void init(){
                this.taskContext = TaskContext.get();
                this.first_execution = false;
                System.out.println("here");
                System.out.println(this.taskContext.taskAttemptId());
            }

            @Override
            public Iterator<String> call(String v1) throws Exception {
                if (this.first_execution){
                    init();
                }

                return Arrays.asList(v1.split(" ")).iterator();
            }
        };
    }

    @Override
    protected HackItExperiment doExecuteNormal() {
        this.sc.getConf().setAppName(this.sc.appName()+" normal");
        this.sc_java
            .textFile(this.input_path)
            .filter(this.generateFunction())
            .saveAsTextFile(this.output_path+"_normal");
        return this;
    }

    @Override
    protected HackItExperiment doExecuteDebug() {
        this.sc.getConf().setAppName(this.sc.appName()+" debug");

        HackItRDD<Object, String> words = HackItRDD.fromJavaRDD(
                this.sc_java
                        .textFile(this.input_path)
                //          .flatMap(line -> Arrays.asList(line.split(" ")).iterator())
        )
                .filter(this.generateFunction())
                .flatMap(this.lineToWordFunction());
        // TODO: try with sniffer
        // .sniffer()



        words.toJavaRDD().saveAsTextFile(this.output_path+"_debug");

        return this;
    }
}
