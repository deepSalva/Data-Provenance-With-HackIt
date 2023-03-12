package org.qcri.hackit.experiments;

import org.qcri.hackit.experiments.HackItExperiment;
import org.qcri.hackit.experiments.instance.GrepHackItExperiment;

import java.util.Set;

public class Main {


    public static void main(String... args){
        //System.setProperty("hadoop.home.dir", "C:\\Users\\Admin\\Developments\\hackit");

        String base_dir = "hackit-experiments"; // => args[0]
        String hostname = "Nobody.local"; // => args[1]
        String input_path = "<path-to-your-file>/SQL_server.txt";// => args[2]
        String output_path = "<path-to-your-file>/map_count2.txt";// => args[3]
        String grep_condition = "Flink";// => args[4]



        System.setProperty("hadoop.home.dir", "/usr/local/Cellar/hadoop/3.2.1");
        HackItExperiment experiment = ((HackItExperiment)
                                    new GrepHackItExperiment("Grep Experiment ", base_dir, hostname, input_path, output_path, grep_condition)
                                )
                               // .addJar("/code/hackit-1.0-SNAPSHOT.jar")
                               // .addJar("/libs/kafka-clients-2.3.0.jar")
        ;


        String type = "debug"; // args[5]
        if(type.compareToIgnoreCase("both") == 0){
            experiment.asNormal().asDebug();
        }else if (type.compareToIgnoreCase("normal") == 0){
            experiment.asNormal();
        }else if(type.compareToIgnoreCase("debug") == 0) {
            experiment.asDebug();
        }

        experiment.execute();
        System.out.println(HackItExperiment.printTimes());

        /*
        Set<Thread> threadSet = Thread.getAllStackTraces().keySet();            ;


        while(threadSet.size()> 3) {
            threadSet = Thread.getAllStackTraces().keySet();
            threadSet.stream().map(thread -> thread.getName()).forEach(System.out::println);
            System.out.println();
            System.out.println();
            System.out.println();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        */
    }

}
