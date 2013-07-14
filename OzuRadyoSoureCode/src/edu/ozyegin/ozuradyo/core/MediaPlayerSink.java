package edu.ozyegin.ozuradyo.core;

import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class MediaPlayerSink implements IStreamSink, IStatistics{
    private final AtomicBoolean initialized = new AtomicBoolean(false);
    private List<String> headers;
    private PipedInputStream pis;
    private PipedOutputStream pos;
    private AtomicBoolean stopRequested =  new AtomicBoolean(false);

    public MediaPlayerSink() throws Exception {
        pis = new PipedInputStream();
        pos = new PipedOutputStream(pis);
        System.out.println();
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
    public void flush() {
        stopRequested.set(true);
    }

    public PipedInputStream getPipedInputStream() {
        return pis;
    }

    public Statistics getStatistics() throws Exception {
        return new Statistics(0,0);
    }
}
