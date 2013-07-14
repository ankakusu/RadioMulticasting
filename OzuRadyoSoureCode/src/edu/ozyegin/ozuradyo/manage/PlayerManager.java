
package edu.ozyegin.ozuradyo.manage;

import edu.ozyegin.ozuradyo.core.MediaPlayerSink;
import edu.ozyegin.ozuradyo.core.RadioCore;
import edu.ozyegin.ozuradyo.core.Statistics;
import edu.ozyegin.ozuradyo.media.MP3Player;
import java.io.InputStream;
import java.net.Socket;
/**
 *
 * @author ugurk
 */

public class PlayerManager extends Thread{
    
    private Statistics stats = new Statistics(0, 0);
    private Updater updater = null;
    private long sleeptime = 500;
    private MP3Player player;
    /**
     * PlayerManager manages {@link MediaPlayerSink} and {@link MP3Player}. Also It feeds UI with {@link Statistics}.
     * @param up    Player Manager takes a {@link Updater} implemented class for updating player state and network traffic.
     * @param timer Defines time interval to read {@link Statistics} object.
     */
    public PlayerManager(Updater up, Long timer){
        this.updater = up;
        this.sleeptime = timer;
    }
    
    @Override
    public void run(){
        
        try {
            
            MediaPlayerSink mps = new MediaPlayerSink();
            
            RadioCore rt = new RadioCore(mps);
            
            rt.start();
            player = new MP3Player(mps.getPipedInputStream());
            player.start();
            
            if(updater != null){
                while(true){
                    this.stats = rt.getStatistics();
                    Thread.sleep(sleeptime);
                    updater.updateStats(this.stats);
                }
            }

            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private InputStream getLocalStream(){
        InputStream is;
        System.out.println("Searching local output stream...");
        while(true){
            try{
                Socket s = new Socket("127.0.0.1",27000);
                is = s.getInputStream();
                System.out.println("Local input stream created, returning...");
                break;
            }
            catch(Exception e){
                
            }
        }
        return is;
    }

    public Statistics getStats() {
        return stats;
    }
    
    public void setVolume(float f){
        player.setVolume(f);
    }
    
}
