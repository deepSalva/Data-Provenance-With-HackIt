package org.qcri.hackit.core.tags;

public class DebugTag extends HackItTag  {

    private static DebugTag seed = null;

    private DebugTag(){
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
            seed = new DebugTag();
        }
        return seed;
    }

    @Override
    public int hashCode() {
        return 2;
    }
}
