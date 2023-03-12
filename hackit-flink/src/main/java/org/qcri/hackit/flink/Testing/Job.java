package org.qcri.hackit.flink.Testing;


import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.utils.ParameterTool;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static org.qcri.hackit.flink.Testing.wordCountForwardTraceTest.runWithHackit;
import static org.qcri.hackit.flink.Testing.wordCountForwardTraceTest.runWordCountNormal;

public class Job {
    public static void main(String[] args) throws Exception{
        final ParameterTool params = ParameterTool.fromArgs(args);
        String inputPath = params.get("input", "/Users/joschavonhein/Workspace/bdapro-ss20-dataprov/hackit-src/hackit-flink/Input/hamlet.txt");
        String outputPath = params.get("outputDir","/Users/joschavonhein/Workspace/bdapro-ss20-dataprov/hackit-src/hackit-flink/Output/wordCountExample");

        final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        env.getConfig().setGlobalJobParameters(params);
        ArrayList<Long> runtimesHackItBloomFilter = new ArrayList<>();
        ArrayList<Long> runtimesNormal = new ArrayList<>();
        ArrayList<Long> runtimesHackItNormal = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            runtimesHackItBloomFilter.add(runWithHackit(inputPath, outputPath, true).getNetRuntime(TimeUnit.SECONDS));
            runtimesHackItNormal.add(runWithHackit(inputPath, outputPath, false).getNetRuntime(TimeUnit.SECONDS));
            runtimesNormal.add(runWordCountNormal(inputPath, outputPath).getNetRuntime(TimeUnit.SECONDS));
        }

        for (int i = 0; i < 10; i++) {

        }

        System.out.println("runtimes hackit bloomfilter: " + runtimesHackItBloomFilter);
        System.out.println("runtimes hackit normal: " + runtimesHackItNormal);
        System.out.println("runtimes normal: " + runtimesNormal);
    }
}
