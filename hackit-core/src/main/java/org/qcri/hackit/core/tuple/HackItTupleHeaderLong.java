package org.qcri.hackit.core.tuple;

import java.util.ArrayList;

public class HackItTupleHeaderLong extends HackItTupleHeader<Long> {
    static long base;
    // protected ArrayList<Long> children; // stores child ids
    // protected ArrayList<Long> parents; // stores parent ids

    static{
        base = 0;//(new Random()).nextLong();
    }

    public HackItTupleHeaderLong(Long id){
        super(id);
    }

    public HackItTupleHeaderLong() {
        super();
    }

    public HackItTupleHeaderLong(Long id, int child) {
        super(id, child);
    }

    @Override
    public HackItTupleHeader<Long> createChild() {
        return new HackItTupleHeaderLong(this.getId(), this.child++);
    }

    @Override
    protected Long generateID() {
        return base++; // simply increment for new IDs -> should only be used in when starting a new lineage trace!
    }
}
