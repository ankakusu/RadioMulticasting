
public class Statistics {
    private final long receivedBytes;
    private final long sentBytes;

    public Statistics(long sentBytes, long receivedBytes) {
        this.sentBytes = sentBytes;
        this.receivedBytes = receivedBytes;
    }

    public long getReceivedBytes(){
        return receivedBytes;
    }

    public long getSentBytes(){
        return sentBytes;
    }
}
