import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class InternetSource implements IStreamSource {
    private BufferedInputStream bufferedStream;
    private List<String> headers;
    private List<IStreamSink> sinks;
    private Type type;

    public InternetSource(URL url, List<IStreamSink> sinks) throws IOException {
        type = Type.INTERNET_SOURCE;
        // Connect
        URLConnection conn = url.openConnection();
        InputStream stream = conn.getInputStream();
        bufferedStream = new BufferedInputStream(stream);
        // Get Headers
        headers = readHeaders();
        this.sinks = sinks;
        thread.run();
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
    public byte[] read() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[BUFFER_SIZE];
        int nRead = 0;

        if ((nRead = bufferedStream.read(buffer)) > 0) {
            baos.write(buffer, 0, nRead);
        } else {

        }
        return baos.toByteArray();  
    }
    
    private Thread thread = new Thread(){
        public void run(){
            while(true){
                try {
                    byte[] buf = read();
                    for(IStreamSink sink: sinks){
                        if(!sink.isInitialized()){
                            sink.initialize(headers);
                        }
                        sink.write(buf);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
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
