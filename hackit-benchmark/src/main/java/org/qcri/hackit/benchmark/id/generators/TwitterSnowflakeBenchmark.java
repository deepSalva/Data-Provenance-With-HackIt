package org.qcri.hackit.benchmark.id.generators;


import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.qcri.hackit.core.id.HackItIDGenerator;
import org.qcri.hackit.core.id.generators.TwitterSnowflake;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Fork(value = 2, jvmArgs = {"-Xms2G", "-Xmx2G"})
@Warmup(iterations = 1)
@Measurement(iterations = 3)
public class TwitterSnowflakeBenchmark {

    HackItIDGenerator<Integer, Long> generator;

    @Setup
    public void setup(){
        this.generator = new TwitterSnowflake();
    }


    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public void generate_n_ID(Blackhole bh){
        bh.consume(this.generator.generateId());
    }

}
