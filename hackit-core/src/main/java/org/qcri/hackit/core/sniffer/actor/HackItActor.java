package org.qcri.hackit.core.sniffer.actor;

import scala.Tuple2;

import java.io.Serializable;

public interface HackItActor<T> extends Serializable {

    public boolean is_sendout(T value);
}
