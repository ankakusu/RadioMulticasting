
package edu.ozyegin.ozuradyo.core;

import java.net.URL;

/**
 *
 * @author ugurk
 */
public class ProtocolSettings {
    
    private String udp = "channel-udp.xml";
    private String tcp = "channel-tcp.xml";
    private String stream = "stream.xml";
    
    public URL getUdpXML(){
        URL u = ProtocolSettings.class.getResource(udp);
        return u;
    }
    
    public URL getTcpXML(){
        URL u = ProtocolSettings.class.getResource(tcp);
        return u;
    }
    
    public URL getStreamXML(){
        URL u = ProtocolSettings.class.getResource(stream);
        return u;
    }
}
