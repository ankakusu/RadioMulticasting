import org.jgroups.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class BroadcastStreamSink extends ReceiverAdapter implements StreamSink {
    private final AtomicBoolean initialized = new AtomicBoolean(false);
    JChannel channel;
    List<String> headers;
    Set<Address> initializedMembers;

    public BroadcastStreamSink(String dataClusterName) throws Exception {
        channel = new JChannel("channel-udp.xml");
        channel.connect(dataClusterName);
        channel.setReceiver(this);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void viewAccepted(View view){
        System.out.println("Members: " + view);

    }

    @Override
    public void write(byte[] b) throws Exception {
        channel.send(null, new DataMessage(b).pack());
    }

    public boolean isInitialized() {
        return initialized.get();
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
    
}
