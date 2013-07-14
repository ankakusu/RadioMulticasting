package edu.ozyegin.ozuradyo.core;

import java.util.List;
import org.jgroups.Address;


public class Statistics {
    private final long receivedBytes;
    private final long sentBytes;
    private final List<Address> view;

    public Statistics(long sentBytes, long receivedBytes) {
        this.sentBytes = sentBytes;
        this.receivedBytes = receivedBytes;
        this.view = null;
    }
    
    public Statistics(long sentBytes, long receivedBytes, List<Address> view) {
        this.sentBytes = sentBytes;
        this.receivedBytes = receivedBytes;
        this.view = view;
    }

    public long getReceivedBytes(){
        return receivedBytes;
    }

    public long getSentBytes(){
        return sentBytes;
    }
    
    public List<Address> getMembers(){
        return view;
    }
}
