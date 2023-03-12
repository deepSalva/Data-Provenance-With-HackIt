package org.qcri.hackit.core.sniffer;

import org.qcri.hackit.core.sniffer.shipper.HackItShipper;
import org.qcri.hackit.core.sniffer.shipper.receiver.HackItReceiver;
import org.qcri.hackit.core.sniffer.clone.HackItCloner;
import org.qcri.hackit.core.sniffer.inject.HackItInjector;
import org.qcri.hackit.core.sniffer.actor.HackItActor;
import org.qcri.hackit.core.sniffer.shipper.sender.HackItSender;
import org.qcri.hackit.core.sniffer.sniff.HackItSniff;
import org.qcri.hackit.core.tuple.HackItTuple;

import java.io.Serializable;
import java.util.Iterator;
import java.util.function.Function;

public class HackItSniffer<K, T, SentType, Sender extends HackItSender<SentType>, Receiver extends HackItReceiver<HackItTuple<K,T>> > implements Function<HackItTuple<K, T>, Iterator<HackItTuple<K, T>>>, Serializable {

    private transient boolean not_first = false;
    private HackItInjector<HackItTuple<K, T>> hackItInjector;

    private HackItActor<HackItTuple<K, T>> actorFunction;

    private HackItShipper<HackItTuple<K, T>, SentType, Sender, Receiver> shipper;

    private HackItSniff<HackItTuple<K, T>> hackItSniff;
    private HackItCloner<HackItTuple<K, T>, SentType> hackItCloner;

    public HackItSniffer(HackItInjector<HackItTuple<K, T>> hackItInjector, HackItActor<HackItTuple<K, T>> actorFunction, HackItShipper<HackItTuple<K, T>, SentType, Sender, Receiver> shipper, HackItSniff<HackItTuple<K, T>> hackItSniff, HackItCloner<HackItTuple<K, T>, SentType> hackItCloner) {
        this.hackItInjector = hackItInjector;
        this.actorFunction = actorFunction;
        this.shipper = shipper;
        this.hackItSniff = hackItSniff;
        this.hackItCloner = hackItCloner;
        this.not_first = false;
    }

    public HackItSniffer() {
        //TODO this over configuration file
        this.not_first = false;
    }

    @Override
    public Iterator<HackItTuple<K, T>> apply(HackItTuple<K, T> ktHackItTuple) {
        if(!this.not_first){
            this.shipper.subscribeAsProducer();
            this.shipper.subscribeAsConsumer();
            this.not_first = true;
        }

        if(this.hackItSniff.sniff(ktHackItTuple)){
            if(this.actorFunction.is_sendout(ktHackItTuple)){
                this.shipper.publish(
                    this.hackItCloner.clone(ktHackItTuple)
                );
            }
        }
        Iterator<HackItTuple<K, T>> inyection = this.shipper.getNexts();

        return this.hackItInjector.inject(ktHackItTuple, inyection);
    }

    public HackItSniffer<K, T, SentType, Sender, Receiver> setHackItInjector(HackItInjector<HackItTuple<K, T>> hackItInjector) {
        this.hackItInjector = hackItInjector;
        return this;
    }

    public HackItSniffer<K, T, SentType, Sender, Receiver> setActorFunction(HackItActor<HackItTuple<K, T>> actorFunction) {
        this.actorFunction = actorFunction;
        return this;
    }

    public HackItSniffer<K, T, SentType, Sender, Receiver> setShipper(HackItShipper<HackItTuple<K, T>, SentType, Sender, Receiver> shipper) {
        this.shipper = shipper;
        return this;
    }

    public HackItSniffer<K, T, SentType, Sender, Receiver> setHackItSniff(HackItSniff<HackItTuple<K, T>> hackItSniff) {
        this.hackItSniff = hackItSniff;
        return this;
    }

    public HackItSniffer<K, T, SentType, Sender, Receiver> setHackItCloner(HackItCloner<HackItTuple<K, T>, SentType> hackItCloner) {
        this.hackItCloner = hackItCloner;
        return this;
    }

    @Override
    public String toString() {
        return "HackItSniffer{" +
                "\nfirst=" + not_first +
                ",\n hackItInjector=" + hackItInjector +
                ",\n actorFunction=" + actorFunction +
                ",\n shipper=" + shipper +
                ",\n hackItSniff=" + hackItSniff +
                ",\n hackItCloner=" + hackItCloner +
                "\n}";
    }
}
