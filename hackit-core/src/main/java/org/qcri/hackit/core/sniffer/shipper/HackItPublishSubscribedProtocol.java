package org.qcri.hackit.core.sniffer.shipper;

public interface HackItPublishSubscribedProtocol {

    public HackItPublishSubscribedProtocol addTopic(String... topic);
    public HackItPublishSubscribedProtocol addExchange(String exchange);
}
