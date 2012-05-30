import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class LocalSink implements IStreamSink,IStatistics {
    private final AtomicBoolean initialized = new AtomicBoolean(false);
    Socket socket;
    OutputStream outputStream;
    ServerSocket serverSocket;
    List<String> headers;
    Map<Socket,OutputStream> clients = new HashMap<Socket, OutputStream>();
    private long sentBytes;
    
    public LocalSink(String url, int port) throws IOException {
        serverSocket = new ServerSocket(
                port, 32, InetAddress.getByName(url));
        serverThread.start();
    }

    @Override
    public Statistics getStatistics() throws Exception {
        // TODO: Add a reasonable Statistics class
        return new Statistics(0,0);
    }
    
    @Override
    public void write(byte[] b) throws Exception {
        if( socket != null){
            try {
                //display(b.length);
                sentBytes = sentBytes + b.length;
                for (Socket socket : clients.keySet())
                    clients.get(socket).write(b);// outputStream.write(b);
            } catch (IOException e) {
                if(!socket.isClosed())
                    socket.shutdownOutput();
                socket.close();
                clients.remove(socket);
            }
        }
    }

    @Override
    public void initialize(List<String> h) {
        System.out.println("LocalSink: Headers are initialized");
        display("headers",h);
        headers = h;
        initialized.set(true);
    }

    private Thread serverThread = new Thread(){
        public void run(){
            while(true){
                try {
                    display("Waiting for incoming connections...");
                    socket = serverSocket.accept();
                    outputStream = new DataOutputStream(
                            socket.getOutputStream());
                    writeHeaders();
                    clients.put(socket,outputStream);
                    System.out.println("Received connection from " + socket.getInetAddress());
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    void writeHeaders() throws IOException {
        StringBuilder builder = new StringBuilder();
        for (String header : headers) {
            builder.append(header);
            builder.append(NEWLINE);
        }
        builder.append(NEWLINE);
        outputStream.write(builder.toString().getBytes());
    }

    private void display(String title, List<String> lines) {
        System.out.println("### " + this.getClass().toString() + "<" + title + "> ###");
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

    public boolean isInitialized() {
        return initialized.get();
    }


}
