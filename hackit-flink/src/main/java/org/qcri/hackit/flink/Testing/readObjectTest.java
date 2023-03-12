package org.qcri.hackit.flink.Testing;

import org.apache.flink.api.java.utils.ParameterTool;

public class readObjectTest {
    public static void main(String[] args) {

        final ParameterTool params = ParameterTool.fromArgs(args);
        String inputPath = params.get("input", "/Users/joschavonhein/Workspace/bdapro-ss20-dataprov/hackit-src/hackit-flink/Input/hamlet.txt");
        String outputPath = params.get("outputDir","/Users/joschavonhein/Workspace/bdapro-ss20-dataprov/hackit-src/hackit-flink/Output/wordCountExample");
        try {
            wordCountForwardTraceTest.runWithHackit(inputPath, outputPath, true);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
