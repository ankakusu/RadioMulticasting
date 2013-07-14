package edu.ozyegin.ozuradyo.core;

public interface IStreamSource {
    final String NEWLINE = "\r\n";
    final String LOCALHOST = "127.0.0.1";
    final int BUFFER_SIZE = 8192;
    byte[] read() throws Exception;
    void flush();

}
