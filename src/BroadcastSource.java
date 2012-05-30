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


    public BroadcastSource(String groupName, List<IStreamSink> sinks) throws Exception {
        // Create data channel
        channel = new JChannel("channel-udp.xml");
        channel.connect(groupName);
        channel.setReceiver(this);

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
        display("headers",headers);
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
        System.out.println("Members: " + view);
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
    public Statistics getStatistics() throws Exception {
//        System.out.println("<Broadcast source> Sent Bytes " + channel.getSentBytes() +
//        "\n<Broadcast source> Received Bytes "+ channel.getReceivedBytes());
        return new Statistics(channel.getSentBytes(), channel.getReceivedBytes());
    }
    
    private Thread thread = new Thread(){
        public void run(){
             while(true){
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



}
