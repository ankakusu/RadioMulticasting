import org.jgroups.JChannel;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RadioCore implements IStatistics{
    //Create new control channel
    String controlClusterName = "RadioOzuControl";
    private String dataClusterName = "RadioOzu";
    private URL u = RadioCore.class.getResource("channel-udp.xml");
    private List<IStatistics> observables = new ArrayList<IStatistics>();

    List<IStreamSink> sinks = new ArrayList<IStreamSink>();
    IStreamSource source;

    public void startStream() throws Exception {
        JChannel channel =  new JChannel(u);
        channel.connect(controlClusterName);

        sinks.add(new LocalSink("127.0.0.1", 27000));

        if(channel.getView().getMembers().size() == 1){
            sinks.add(new BroadcastSink(dataClusterName));
        }

        //Create sources
        String url = "http://livestream.srg-ssr.ch/1/rsj/mp3_128";
        source = !(channel.getView().getMembers().size() > 1) ?
                new InternetSource(new URL(url), sinks) :
                new BroadcastSource(dataClusterName, sinks);

        try{
            observables.add((IStatistics) source);
        }catch (Exception e){}
    }

    @Override
    public Statistics getStatistics() throws Exception {
        //observables.add(sinks);
        // if the source is a broadcast stream source
        // return the Statistics for this object
        //

        return null;
    }
}
