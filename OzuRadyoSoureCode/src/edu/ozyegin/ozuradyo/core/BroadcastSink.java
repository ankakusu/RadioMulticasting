package edu.ozyegin.ozuradyo.core;

import org.jgroups.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 
 * @author Yaprak Ayazoglu
 */
public class BroadcastSink extends ReceiverAdapter implements IStreamSink,IStatistics {
    private final AtomicBoolean initialized = new AtomicBoolean(false);
    private JChannel channel;
    private List<String> headers;
    private Set<Address> initializedMembers;
    
    public BroadcastSink(String dataClusterName) throws Exception {
        ProtocolSettings ps = new ProtocolSettings();
        channel = new JChannel( ps.getStreamXML() );
        init(dataClusterName);
    }
    
    /**
     * Initialization for {@link JChannel}.
     * @param dataClusterName   The data cluster name that will be connected.
     * @throws Exception    {@link JChannel} connection error.
     */
    private void init(String dataClusterName) throws Exception{
        channel.setReceiver(this);
        channel.connect(dataClusterName);
        initializedMembers = new HashSet<Address>();        
    }

    @Override
    public void receive(Message msg){
        if (msg.getSrc() == channel.getAddress()) return;
        try {
            ClusterMessage cm = ClusterMessage.unpack(msg.getBuffer());
            switch (cm.getType()) {
                case JOIN:
                    System.out.println("Received message type " + cm.getType());
                    channel.send(msg.getSrc(), new HeadersMessage(headers).pack());
                    break;
                case DATA:
                    System.out.println("Received message type " + cm.getType());
                    break;
                case HEADERS:
                    System.out.println("Received message type " + cm.getType());
                    break;
                default:
                    throw new Exception("Unknown cluster message type:" + cm.getType());
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void viewAccepted(View view){
        System.out.println("** DataChannel View " + view);
    }

    @Override
    public void write(byte[] b) throws Exception {
        channel.send(null, new DataMessage(b).pack());
    }

    public boolean isInitialized() {
        return initialized.get();
    }

    @Override
    public void flush() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void initialize(List<String> h) {
        headers = h;
        initialized.set(true);
    }

    private void display(String title, List<String> lines) {
        System.out.println("### " + this.getClass().toString() + "<" + title + "> ###");
        for (String line : lines)
            System.out.println("'" + line + "'");
        System.out.println("### " + this.getClass().toString() + " </" + title + "> ###");
    }

    private void display(String title, Object obj) {
        System.out.println("### "+ this.getClass().toString() + title + ": '" + obj + "'");
    }

    private void display(Object obj) {
        System.out.println("### "+ this.getClass().toString() + " " + obj);
    }

    private void display() {
        System.out.println();
    }

    @Override
    public Statistics getStatistics() throws Exception {
//        System.out.println("<Broadcast sink> Sent Bytes " + channel.getSentBytes() +
//                "\n<Broadcast sink> Received Bytes "+ channel.getReceivedBytes());
        return new Statistics(channel.getSentBytes(),channel.getReceivedBytes());
    }
}
