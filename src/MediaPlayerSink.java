import edu.ozyegin.ozuradyo.MP3Player;

import java.net.URL;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class MediaPlayerSink implements StreamSink{
    private final AtomicBoolean initialized = new AtomicBoolean(false);
    List<String> headers;
    MP3Player mp;
    
    public MediaPlayerSink(URL url) throws Exception {
        mp = new MP3Player(url);
        mp.start();
    }

    @Override
    public void write(byte[] b) throws Exception {

    }

    @Override
    public void initialize(List<String> h) {
        System.out.println("LocalStreamSink: Headers are initialized");
        display("headers",h);
        headers = h;
        initialized.set(true);
    }

    @Override
    public boolean isInitialized() {
        return initialized.get();
    }

    private void display(String title, List<String> lines) {
        System.out.println("### " + this.getClass().toString() + " <" + title + "> ###");
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
