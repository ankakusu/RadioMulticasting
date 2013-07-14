package edu.ozyegin.ozuradyo.core;

import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public final class BroadcastSource extends ReceiverAdapter implements IStreamSource,IStatistics {
    private AtomicBoolean initialized = new AtomicBoolean(false);
    private JChannel channel;
    private List<IStreamSink> sinks;
    private BlockingQueue<byte[]> buffers = new LinkedBlockingQueue<byte[]>();
    private List<String> headers;
    private AtomicBoolean stopRequested = new AtomicBoolean(false);


    public BroadcastSource(String groupName, List<IStreamSink> sinks) throws Exception {
        // Create data channel
        ProtocolSettings ps = new ProtocolSettings();
        channel = new JChannel( ps.getStreamXML() );
        channel.setReceiver(this);
        channel.connect(groupName);

        channel.send(null,new JoinMessage().pack());
        this.sinks = sinks;
        thread.start();
    }

    @Override
    public void receive(Message msg){
        if (msg.getSrc() == channel.getAddress()) return;
        try {
            ClusterMessage cm = ClusterMessage.unpack(msg.getBuffer());
            switch (cm.getType()){
                case DATA:
                    receive((DataMessage)cm);
                    break;
                case HEADERS:
                    receive((HeadersMessage)cm);
                    break;
                case JOIN:
                    System.out.println("Join from " + msg.getSrc());
                    break;
                default:
                    throw new Exception("Unknown cluster message type:" + cm.getType());
            }
        } catch (Exception e) {
            e.printStackTrace();  
        }
    }

    private void receive(HeadersMessage hm) {
        headers = hm.getHeaders();
        // display("headers",headers);
        initialized.set(true);
    }

    private void receive(DataMessage dm) {
        if (isInitialized()) {
            buffers.add(dm.getData());
        }
    }

    public boolean isInitialized() {
        return initialized.get();
    }

    @Override
    public void viewAccepted(View view){
        System.out.println("**DataChannel View " + view);
    }

    @Override
    public byte[] read() throws Exception {
        while (true) {
            byte[] buf = buffers.poll(5, TimeUnit.SECONDS);
            if (buf != null)
                return buf;
        }
    }

    @Override
    public void flush() {
        for(IStreamSink sink : sinks)
            sink.flush();
        stopRequested.set(true);
        channel.disconnect();
    }

    @Override
    public Statistics getStatistics() throws Exception {
//        System.out.println("<Broadcast source> Sent Bytes " + channel.getSentBytes() +
//        "\n<Broadcast source> Received Bytes "+ channel.getReceivedBytes());
        return new Statistics(channel.getSentBytes(), channel.getReceivedBytes());
    }
    
    private Thread thread = new Thread(){
        public void run(){
             while(!stopRequested.get()){
                 try {
                     byte[] buf = read();
                     for (IStreamSink sink: sinks){
                         if(!sink.isInitialized()){
                             sink.initialize(headers);
                         }
                         sink.write(buf);
                     }
                 } catch (Exception e) {
                     e.printStackTrace();
                 }
             }
        }
    };

}
