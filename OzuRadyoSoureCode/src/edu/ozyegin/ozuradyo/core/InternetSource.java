package edu.ozyegin.ozuradyo.core;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class InternetSource implements IStreamSource, IStatistics {
    private BufferedInputStream bufferedStream;
    private List<String> headers;
    private List<IStreamSink> sinks;
    private long readBytes;
    private AtomicBoolean stopRequested =  new AtomicBoolean(false);

    public InternetSource(URL url, List<IStreamSink> sinks) throws IOException {
        // Connect
        URLConnection conn = url.openConnection();
        InputStream stream = conn.getInputStream();
        bufferedStream = new BufferedInputStream(stream);
        // Get Headers
        headers = readHeaders();
        this.sinks = sinks;
        thread.start();
    }

    private List<String> readHeaders() throws IOException {
        List<String> lines = new ArrayList<String>();
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(bufferedStream));
        String line;
        long nRead = NEWLINE.length();  // For the last empty line.
        bufferedStream.mark(Integer.MAX_VALUE);
        while ((line = reader.readLine()) != null && !(line.isEmpty())) {
            nRead += line.getBytes().length + NEWLINE.length();
            lines.add(line);
        }
        bufferedStream.reset();
        long nSkipped = bufferedStream.skip(nRead);
        assert (nSkipped == nRead);
        return lines;
    }

    @Override
    public Statistics getStatistics() throws Exception {
        // TODO: Add a reasonable Statistics class
        return new Statistics(0,0);
    }

    @Override
    public byte[] read() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[BUFFER_SIZE];
        int nRead = 0;

        if ((nRead = bufferedStream.read(buffer)) > 0) {
            readBytes = readBytes + nRead;
            baos.write(buffer, 0, nRead);
        } else {

        }
        return baos.toByteArray();  
    }

    @Override
    public void flush() {
        for(IStreamSink sink: sinks)
            sink.flush();
        stopRequested.set(true);
    }

    private Thread thread = new Thread(){
        @Override
        public void run(){
            while(!stopRequested.get()){
                try {
                    byte[] buf = read();
                    //display("buflen:", buf.length);
                    for(IStreamSink sink: sinks){
                        if(!sink.isInitialized()){
                            sink.initialize(headers);
                        }
                        sink.write(buf);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    void display(String title, List<String> lines) {
        System.out.println("###" + this.getClass().toString() + "<" + title + "> ###");
        for (String line : lines)
            System.out.println("'" + line + "'");
        System.out.println("### </" + title + "> ###");
    }

    void display(String title, Object obj) {
        System.out.println("### " + title + ": '" + obj + "'");
    }

    void display(Object obj) {
        System.out.println("### " + obj);
    }

    void display() {
        System.out.println();
    }


}
