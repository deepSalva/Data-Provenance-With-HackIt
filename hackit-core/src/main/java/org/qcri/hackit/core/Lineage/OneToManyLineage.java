package org.qcri.hackit.core.Lineage;

import org.apache.hadoop.util.bloom.BloomFilter;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class OneToManyLineage implements LineageElement<Long, List<Long>>, Serializable {

    long input;
    LinkedList<Long> output;

    public OneToManyLineage(long input, LinkedList<Long> output) {
        this.input = input;
        this.output = output;
    }

    public Long getInput() {
        return input;
    }

    public List<Long> getOutput() {
        return output;
    }

    private void writeObject(java.io.ObjectOutputStream out)
            throws IOException {
        out.writeLong(input);
        out.writeObject(output);
    }
    private void readObject(java.io.ObjectInputStream in)
            throws IOException, ClassNotFoundException {
        input = in.readLong();
        output = (LinkedList<Long>) in.readObject();
    }
    private void readObjectNoData()
            throws ObjectStreamException {};
}
