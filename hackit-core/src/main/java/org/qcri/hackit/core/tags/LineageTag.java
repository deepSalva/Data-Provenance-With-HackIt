package org.qcri.hackit.core.tags;

import java.util.function.Function;

public class LineageTag extends HackItTag {

    @Override
    public HackItTag getInstance() {
        return null;
    }

    @Override
    public boolean hasCallback() {
        return false;
    }

    @Override
    public boolean isHaltJob() {
        return false;
    }

    @Override
    public boolean isSendOut() {
        return false;
    }

    @Override
    public boolean isSkip() {
        return false;
    }
}
