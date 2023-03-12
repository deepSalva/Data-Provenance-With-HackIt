package org.qcri.hackit.core.sniffer.sniff;

import org.qcri.hackit.core.tags.HackItTag;

import java.util.HashSet;
import java.util.Set;

public class SetTagsSniffHackItFunction implements HackItSniff {

    public Set<HackItTag> tags2sniff;

    public SetTagsSniffHackItFunction(){
        this.tags2sniff = new HashSet<>();
    }

    public boolean sniff(HackItTag tag){
        return this.tags2sniff.contains(tag);
    }

    public void addTag2sniff(HackItTag tag) {
        this.tags2sniff.add(tag);
    }

    @Override
    public boolean sniff(Object input) {
        return false;
    }
}
