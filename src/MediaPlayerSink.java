import edu.ozyegin.ozuradyo.MP3Player;

import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class MediaPlayerSink implements IStreamSink,IStatistics{
    private final AtomicBoolean initialized = new AtomicBoolean(false);
    private List<String> headers;
    PipedInputStream pis;
    PipedOutputStream pos;

    MediaPlayerSink(MP3Player mp3Player) throws Exception {
        pis = new PipedInputStream();
        pos = new PipedOutputStream(pis);
        //TODO: Add pis to InputStream of MP2Player

    }

    @Override
    public void write(byte[] b) throws Exception {
        pos.write(b);
    }

    @Override
    public void initialize(List<String> h) {
        headers = h;
        initialized.set(true);
    }

    @Override
    public boolean isInitialized() {
        return initialized.get();
    }

    @Override
    public Statistics getStatistics() throws Exception {
        return new Statistics(0,0);
    }
}
