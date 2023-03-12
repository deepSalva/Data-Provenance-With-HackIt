package org.qcri.hackit.core.tags;

public class LogTag extends HackItTag  {
    private static LogTag seed = null;

    public LogTag(){
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
            seed = new LogTag();
        }
        return seed;
    }

    @Override
    public int hashCode() {
        return 4;
    }
}
