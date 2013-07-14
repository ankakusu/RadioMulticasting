
package edu.ozyegin.ozuradyo.manage;

import edu.ozyegin.ozuradyo.core.Statistics;

/**
 *
 * @author ugurk
 */
public class ConsolePlayer {
    
    public void play(){
        PlayerManager pm = new PlayerManager(new ConsoleUpdater(), 1000L);
        pm.start();
    }
    
    public static void main(String args[]){
        PlayerManager pm = new PlayerManager(new ConsoleUpdater(), 1000L);
        pm.start();
    }
    
}

class ConsoleUpdater implements Updater{

    public void updateStats(Statistics s) {
        String nl = "down: "+s.getReceivedBytes()+" , up: "+s.getSentBytes();
        System.out.println(nl);
    }
    
    
    
}
