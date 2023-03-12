package org.qcri.hackit.core.tags;

public class DisplayTag extends HackItTag  {
    private static DisplayTag seed = null;

    private DisplayTag(){
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
            seed = new DisplayTag();
        }
        return seed;
    }

    @Override
    public int hashCode() {
        return 3;
    }
}
