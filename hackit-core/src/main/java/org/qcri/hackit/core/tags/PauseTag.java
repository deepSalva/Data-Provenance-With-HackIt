package org.qcri.hackit.core.tags;

public class PauseTag extends HackItTag  {
    private static PauseTag seed = null;

    private PauseTag(){
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
            seed = new PauseTag();
        }
        return seed;
    }

    @Override
    public int hashCode() {
        return 5;
    }
}
