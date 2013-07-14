package edu.ozyegin.ozuradyo.core;

import org.jgroups.JChannel;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Main extends ReceiverAdapter{
    //Create new control channel
    public String controlClusterName = "RadioOzuControl";
    static String dataClusterName = "RadioOzu";
    static JChannel channel;
    static IStreamSource source;
    static String url = "http://205.188.215.229:8008";
    static List<IStreamSink> sinks;


    public static void main(String[] args)  throws Exception {
        Main m = new Main();
        m.startChannel();
    }
    
    public void startChannel(){
        try{
            ProtocolSettings ps = new ProtocolSettings();
            channel = new JChannel( ps.getUdpXML() );
            channel.setReceiver(this);

            channel.connect(controlClusterName);

            sinks = new ArrayList<IStreamSink>();
            sinks.add(new LocalSink("127.0.0.1",27000));

            if(channel.getView().getMembers().size() == 1){
                sinks.add(new BroadcastSink(dataClusterName));
            }

            
            //Create source
            source = !(channel.getView().getMembers().size() > 1) ?
                    new InternetSource(new URL(url), sinks):      // İlk kaynak ise
                    new BroadcastSource(dataClusterName, sinks); // Diğer kaynaklar ise

            //System.out.println("SourceType: " + source.getClass().getName());
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    
    @Override
    public void viewAccepted(View view) {
        System.out.println("** View " + view);
        if(view.getMembers().get(0).equals(channel.getAddress())){
            try {
                if(source!=null && (source.getClass().getName().equals("BroadcastSource"))){
                    source.flush();
                    BroadcastSink broadcastSink = new BroadcastSink(dataClusterName);
                    sinks.add(broadcastSink);
                    source = new InternetSource(new URL(url),sinks);
                }
            } catch (Exception e) {
                System.out.println("Error @ viewAccepted!");
                e.printStackTrace();
            }
        }
    }
}
