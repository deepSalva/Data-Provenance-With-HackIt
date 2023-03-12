package org.qcri.hackit.experiments;

import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaSparkContext;
import org.qcri.hackit.spark.listener.HackItListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class HackItExperiment  {

    protected String name_experiment;
    transient protected SparkConf conf;
    transient protected SparkContext sc;
    transient protected JavaSparkContext sc_java;
    protected String hostname;
    protected String base_dir;
    protected List<String> jars;
    protected boolean execute_normal = false;
    protected boolean execute_debug = false;
    transient private HackItListener listener;

    public static Map<String, Long> times = new HashMap<>();

    public HackItExperiment(){}

    public HackItExperiment(String name, String base_dir, String hostname){
        this.name_experiment = name;
        this.base_dir = base_dir;
        this.hostname = hostname;
        this.jars = new ArrayList<>();
    }

    public HackItExperiment addJar(String jar){
        this.jars.add(jar);
        return this;
    }

    public HackItExperiment asNormal(){
        this.execute_normal = true;
        return this;
    }

    public HackItExperiment asDebug(){
        this.execute_debug = true;
        return this;
    }

    protected HackItExperiment createSparkContext(){
        this.conf = new SparkConf()
            .setAppName(this.name_experiment)
            //.setMaster(String.format("spark://%s:7077", this.hostname))
            .setMaster("local[*]")
            .set("spark.ui.port", "50020")
            .set("spark.executor.cores", "2")
            .set("spark.executor.memory", "1GB")
            .set("spark.hadoop.validateOutputSpecs", "false")
            .setJars(
                jars.stream()
                    .map(jar -> String.format("%s%s", base_dir, jar))
                    .collect(Collectors.toList())
                    .toArray(new String[0])
            )
        ;
        this.sc = new JavaSparkContext(this.conf).sc();
        this.listener = new HackItListener();
        this.sc.addSparkListener(this.listener);
        this.sc_java = new JavaSparkContext(this.sc);
        return this;
    }

    protected HackItExperiment finish(){
        this.sc.stop();
        return this;
    }

    protected abstract HackItExperiment doExecuteNormal();
    protected abstract HackItExperiment doExecuteDebug();

    public HackItExperiment execute(){
        if(this.execute_normal){
            HackItExperiment tmp = this.createSparkContext();
            long start = System.currentTimeMillis();
            tmp.doExecuteNormal();
            long end = System.currentTimeMillis();
            times.put(String.format("%s_%s", this.name_experiment, "normal"), this.listener.end - this.listener.start);
            times.put(String.format("%s_%s", this.name_experiment, "normal_currentTimeMillis"), end - start);
            times.put(
                    String.format("%s_%s", this.name_experiment, "normal_task"),
                    this.listener.tasks.values().stream().reduce(0L, (a,b) -> a + b)/ this.listener.tasks.size()
            );
            tmp.finish();
        }
        if(this.execute_debug){
            HackItExperiment tmp = this.createSparkContext();
            long start = System.currentTimeMillis();
            tmp.doExecuteDebug();
            long end = System.currentTimeMillis();
            times.put(String.format("%s_%s", this.name_experiment, "debug"),  this.listener.end - this.listener.start);
            times.put(String.format("%s_%s", this.name_experiment, "debug_currentTimeMillis"), end - start);
            times.put(
                    String.format("%s_%s", this.name_experiment, "debug_task"),
                    this.listener.tasks.values().stream().reduce(0L, (a,b) -> a + b)/ this.listener.tasks.size()
            );
            tmp.finish();
        }
        return this;
    }

    public static String printTimes(){

       /*
        double overhead =(double) times.entrySet().stream().filter(pair -> pair.getKey().contains("debug")).findFirst().get().getValue()
                                    /
                times.entrySet().stream().filter(pair -> pair.getKey().contains("normal")).findFirst().get().getValue();

        double overhead_task =(double) times.entrySet().stream().filter(pair -> pair.getKey().contains("de_bug_task")).findFirst().get().getValue()
                /
                times.entrySet().stream().filter(pair -> pair.getKey().contains("nor_mal_task")).findFirst().get().getValue();
        */
        return times.entrySet().stream()
            .map(pair -> String.format("#### time of %s \t\t %d", pair.getKey(), pair.getValue()))
            .collect(Collectors.joining("\n"))
            /*
            +
            "\noverhead : " + overhead +
            "\noverhead tasks : " + overhead_task
            */
            ;


    }



}
