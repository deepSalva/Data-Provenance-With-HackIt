package org.qcri.hackit.core.tags;

public class CrashTag extends HackItTag {

    private static CrashTag seed = null;

    private CrashTag(){
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
            seed = new CrashTag();
        }
        return seed;
    }

    @Override
    public int hashCode() {
        return 1;
    }
}
