import org.jgroups.JChannel;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RadioCore extends Thread implements IStatistics{
    //Create new control channel
    private String controlClusterName = "RadioOzuControl";
    private String dataClusterName = "RadioOzu";
    private URL u = RadioCore.class.getResource("channel-udp.xml");
    private List<IStatistics> observables = new ArrayList<IStatistics>();

    private List<IStreamSink> sinks = new ArrayList<IStreamSink>();
    private IStreamSource source;

    @Override
    public void run()  {
        try{
            JChannel channel =  new JChannel(u);
            channel.connect(controlClusterName);

            LocalSink localSink = new LocalSink("127.0.0.1",
                    Integer.parseInt(System.getProperty("radioListenPort")));
            sinks.add(localSink);
            observables.add(localSink);

            if(channel.getView().getMembers().size() == 1){
                BroadcastSink broadcastSink = new BroadcastSink(dataClusterName);
                sinks.add(broadcastSink);
                observables.add(broadcastSink);
            }

            //Create sources
            String url = "http://livestream.srg-ssr.ch/1/rsj/mp3_128";
            source = !(channel.getView().getMembers().size() > 1) ?
                    new InternetSource(new URL(url), sinks) :
                    new BroadcastSource(dataClusterName, sinks);

            observables.add((IStatistics) source);
        }catch (Exception e){
            System.out.println(e.getStackTrace());
        }
    }

    @Override
    public Statistics getStatistics() throws Exception {
        long receivedBytes=0,sentBytes=0;
        for(IStatistics statistics: observables) {
            receivedBytes = receivedBytes + statistics.getStatistics().getReceivedBytes();
            sentBytes = sentBytes + statistics.getStatistics().getSentBytes();
        }
        return new Statistics(sentBytes,receivedBytes);
    }
}
