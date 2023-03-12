package org.qcri.hackit.spark.sniffer;

import org.apache.spark.api.java.function.FlatMapFunction;
import org.qcri.hackit.core.sniffer.HackItSniffer;
import org.qcri.hackit.core.sniffer.actor.HackItActor;
import org.qcri.hackit.core.sniffer.clone.HackItCloner;
import org.qcri.hackit.core.sniffer.inject.HackItInjector;
import org.qcri.hackit.core.sniffer.shipper.HackItShipper;
import org.qcri.hackit.core.sniffer.shipper.receiver.HackItReceiver;
import org.qcri.hackit.core.sniffer.shipper.sender.HackItSender;
import org.qcri.hackit.core.sniffer.sniff.HackItSniff;
import org.qcri.hackit.core.tuple.HackItTuple;

import java.util.Iterator;

public class HackItSnifferSpark<K, T, SentType, Sender extends HackItSender<SentType>, Receiver extends HackItReceiver<HackItTuple<K,T>>>
                    extends HackItSniffer<K, T, SentType, Sender, Receiver>
                    implements FlatMapFunction<HackItTuple<K, T>, HackItTuple<K, T>> {

    public HackItSnifferSpark(HackItInjector<HackItTuple<K, T>> hackItInjector, HackItActor<HackItTuple<K, T>> actorFunction, HackItShipper<HackItTuple<K, T>, SentType, Sender, Receiver> shipper, HackItSniff<HackItTuple<K, T>> hackItSniff, HackItCloner<HackItTuple<K, T>, SentType> hackItCloner) {
        super(hackItInjector, actorFunction, shipper, hackItSniff, hackItCloner);
    }

    public HackItSnifferSpark(){
        super();
    }

    @Override
    public Iterator<HackItTuple<K, T>> call(HackItTuple<K, T> ktHackItTuple) throws Exception {
        return this.apply(ktHackItTuple);
    }
}
