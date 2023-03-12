package org.qcri.hackit.core.tags;

public class SkipTag extends HackItTag  {
    private static SkipTag seed = null;

    private SkipTag(){
        super();
    }

    @Override
    public boolean isSendOut() {
        return false;
    }

    @Override
    public boolean isSkip() {
        return false;
    }

    @Override
    public boolean isHaltJob() {
        return false;
    }

    @Override
    public boolean hasCallback() {
        return false;
    }

    @Override
    public HackItTag getInstance() {
        if(seed == null){
            seed = new SkipTag();
        }
        return seed;
    }

    @Override
    public int hashCode() {
        return 6;
    }
}
