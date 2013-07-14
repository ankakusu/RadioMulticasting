
package edu.ozyegin.ozuradyo.media;

/**
 *
 * @author ugurk
 */
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

public class MP3Player extends Thread{
	
	private AudioInputStream audioinputstream = null;
	private Exception mediaexception = null;
	private Boolean hasException = false;
        private SourceDataLine line;

	public MP3Player(URL u) throws UnsupportedAudioFileException, IOException{
            this.audioinputstream = AudioSystem.getAudioInputStream(u);
	}
	
	public MP3Player(InputStream is) throws UnsupportedAudioFileException, IOException{            
            this.audioinputstream = AudioSystem.getAudioInputStream(is);
	}
	
	public MP3Player(File f) throws UnsupportedAudioFileException, IOException{
            this.audioinputstream = AudioSystem.getAudioInputStream(f);
	}
	
	@Override
	public void run(){
            
            try{
                Thread.sleep(4000);
                    System.out.println("AudioInputStream created.");
                    AudioInputStream din = null;
                    AudioFormat baseFormat = audioinputstream.getFormat();
                    System.out.println("Getting decode format...");
                    AudioFormat decodedFormat = new AudioFormat(
                                    AudioFormat.Encoding.PCM_SIGNED,
                                    baseFormat.getSampleRate(), 16, baseFormat.getChannels(),
                                    baseFormat.getChannels() * 2, baseFormat.getSampleRate(),
                                    false);
                    din = AudioSystem.getAudioInputStream(decodedFormat, audioinputstream);
                    // Play now.
                    System.out.println("playing now...");
                    play(decodedFormat, din);
                    audioinputstream.close();
            }
            catch (Exception e) {
                    hasException = true;
                    this.setMediaexception(e);
                    e.printStackTrace();
            }
	}
	
	private void play(AudioFormat targetFormat, AudioInputStream din) throws IOException, LineUnavailableException {
		byte[] data = new byte[4096];
		line = getLine(targetFormat);
		if (line != null) {
			// Start
			line.start();
			int nBytesRead = 0, nBytesWritten = 0;
			while (nBytesRead != -1) {
				nBytesRead = din.read(data, 0, data.length);
				if (nBytesRead != -1)
					nBytesWritten = line.write(data, 0, nBytesRead);
			}
			// Stop
			line.drain();
			line.stop();
			line.close();
			din.close();
		}
	}
        
        public void setVolume(float f){
            if (line.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                FloatControl volume = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
                volume.setValue(f);
            }
        }
	
	private SourceDataLine getLine(AudioFormat audioFormat) throws LineUnavailableException {
		SourceDataLine res = null;
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
		res = (SourceDataLine) AudioSystem.getLine(info);
		res.open(audioFormat);
		return res;
	}
	
	public synchronized Boolean hasException(){
		return hasException;
	}

	public Exception getMediaexception() {
		return mediaexception;
	}

	public void setMediaexception(Exception mediaexception) {
		this.mediaexception = mediaexception;
	}
}
