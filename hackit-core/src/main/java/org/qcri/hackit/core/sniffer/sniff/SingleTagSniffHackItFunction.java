package org.qcri.hackit.core.sniffer.sniff;

import org.qcri.hackit.core.tags.HackItTag;

public class SingleTagSniffHackItFunction implements HackItSniff {

    public HackItTag tags2sniff;

    public boolean sniff(HackItTag tag){
        return this.tags2sniff.equals(tag);
    }

    public void addTag2sniff(HackItTag tag) {
        if(this.tags2sniff != null){
            throw new RuntimeException("The SingleTagSniffHackItFunction already got the tag, if you need more of one tag use SetTagsSniffHackItFunction class");
        }
        this.tags2sniff = tag;
    }

    @Override
    public boolean sniff(Object input) {
        return false;
    }
}
