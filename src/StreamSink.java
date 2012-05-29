import java.util.List;

public interface StreamSink {
    final String NEWLINE = "\r\n";
    void write(byte[] b) throws Exception;
    void initialize(List<String> headers);
    boolean isInitialized();
}
