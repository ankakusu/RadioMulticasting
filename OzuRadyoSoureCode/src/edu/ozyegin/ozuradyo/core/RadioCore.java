package edu.ozyegin.ozuradyo.core;

import org.jgroups.JChannel;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RadioCore extends Thread implements IStatistics{
    //Create new control channel
    private String controlClusterName = "RadioOzuControl";
    private String dataClusterName = "RadioOzu";
    private ProtocolSettings protocolSettings = new ProtocolSettings();
    private URL u = protocolSettings.getUdpXML();
    private List<IStatistics> observables = new ArrayList<IStatistics>();
    private MediaPlayerSink mps;
    private List<IStreamSink> sinks = new ArrayList<IStreamSink>();
    private IStreamSource source;
    private JChannel channel;
    private String url = "http://livestream.srg-ssr.ch/1/rsj/mp3_128";
    private LocalSink localSink;

    public RadioCore(MediaPlayerSink mps){
        this.mps = mps;
    }
    
    @Override
    public void run()  {
        try{
            channel =  new JChannel(u);
            channel.setReceiver(new ReceiverAdapter(){
                @Override
                public void viewAccepted(View view) {
                    System.out.println("** ControlChannel View: " + view);
                    if(view.getMembers().get(0).equals(channel.getAddress())){
                        try {
                            if(source!=null && source instanceof BroadcastSource) {
                                System.out.println("I'm the new master." 
                                        + System.currentTimeMillis());
                                source.flush();
                                System.out.println("source.flush" 
                                        + System.currentTimeMillis());
                                sinks.clear();
                                System.out.println("sinks.clear" 
                                        + System.currentTimeMillis());
                                observables.clear();
                                System.out.println("observables.clear" 
                                        + System.currentTimeMillis());
                                
                                sinks.add(localSink);
                                observables.add(localSink);
                                System.out.println("LocalSink is re-added");
                                
                                BroadcastSink broadcastSink = new BroadcastSink(dataClusterName);
                                sinks.add(broadcastSink);
                                observables.add(broadcastSink);
                                System.out.println("BroadcastSink is re-added");
                                
                                sinks.add(mps);
                                observables.add(mps);
                                System.out.println("MediaPlayer is re-added");
                                
                                source = new InternetSource(new URL(url),sinks);
                                System.out.println("New InternetSource is created.");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

            channel.connect(controlClusterName);
            
            localSink = new LocalSink("127.0.0.1",27000);
            sinks.add(localSink);
            sinks.add(mps);
            observables.add(mps);
            observables.add(localSink);
                
            if(channel.getView().getMembers().size() == 1){
                BroadcastSink broadcastSink = new BroadcastSink(dataClusterName);
                sinks.add(broadcastSink);
                observables.add(broadcastSink);
            }

            //Create sources
            source = !(channel.getView().getMembers().size() > 1) ?
                    new InternetSource(new URL(url), sinks) :
                    new BroadcastSource(dataClusterName, sinks);

            observables.add((IStatistics) source);
        }catch (Exception e){
            System.out.println(e.getStackTrace());
        }
    }

    public Statistics getStatistics() throws Exception {
        long receivedBytes=0,sentBytes=0;
        for(IStatistics statistics: observables) {
            receivedBytes = receivedBytes + statistics.getStatistics().getReceivedBytes();
            sentBytes = sentBytes + statistics.getStatistics().getSentBytes();
        }
        return new Statistics(sentBytes,receivedBytes,channel.getView().getMembers());
    }
}
