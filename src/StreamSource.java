public interface StreamSource {
    final String NEWLINE = "\r\n";
    final String LOCALHOST = "127.0.0.1";
    final int BUFFER_SIZE = 8192;
    byte[] read() throws Exception;
}
