package org.qcri.hackit.core.Lineage;


import org.apache.flink.core.memory.DataInputView;
import org.apache.flink.core.memory.DataOutputView;
import org.apache.hadoop.util.bloom.BloomFilter;
import org.apache.flink.core.io.IOReadableWritable;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;

public class BloomToOneLineage implements LineageElement, Serializable, IOReadableWritable{
    BloomFilter input;
    long output;

    public BloomToOneLineage(BloomFilter input, long output) {
        this.input = input;
        this.output = output;
    }

    public BloomFilter getInput() {
        return input;
    }

    public Long getOutput() {
        return output;
    }

    private void writeObject(java.io.ObjectOutputStream out)
            throws IOException {
        out.writeLong(output);
        // out.writeObject(input);
        input.write(out);
    }
    private void readObject(java.io.ObjectInputStream in)
            throws IOException, ClassNotFoundException {
        output = in.readLong();
        input = new BloomFilter();
        input.readFields(in);
    }
    private void readObjectNoData()
            throws ObjectStreamException {};

    @Override
    public void write(DataOutputView out) throws IOException {
        out.writeLong(output);
        // out.writeObject(input);
        input.write(out);
    }

    @Override
    public void read(DataInputView in) throws IOException {
        output = in.readLong();
        input = new BloomFilter();
        input.readFields(in);
    }
}

