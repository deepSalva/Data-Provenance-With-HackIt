package org.qcri.hackit.core.id;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.security.SecureRandom;
import java.util.Enumeration;


/**
 * Generate the next ID, N depends of the type that is need
 * */
public abstract class HackItIDGenerator<N, O> {

    private boolean is_address_calculated = false;
    protected InetAddress address_host;

    /** This is the identifier of the process, task, or machine, depends of the platform
     * but is use for the generators.
     * */
    protected N identify_process;

    public HackItIDGenerator(){
        this.identify_process = null;
        this.address_host = null;
    }

    public HackItIDGenerator(N identify_process) {
        this.identify_process = identify_process;
        getAddress();
    }

    protected void getAddress(){
        if( ! this.is_address_calculated ){
            try {
                this.address_host = InetAddress.getLocalHost();
            } catch (Exception e) {
                this.address_host = null;
            }
        }
    }

    protected static int createNodeId() {
        int nodeId;
        try {
            StringBuilder sb = new StringBuilder();
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                byte[] mac = networkInterface.getHardwareAddress();
                if (mac != null) {
                    for(int i = 0; i < mac.length; i++) {
                        sb.append(String.format("%02X", mac[i]));
                    }
                }
            }
            nodeId = sb.toString().hashCode();
        } catch (Exception ex) {
            nodeId = (new SecureRandom().nextInt());
        }
        return nodeId;
    }

    public abstract O generateId();
}
