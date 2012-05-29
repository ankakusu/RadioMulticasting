
public class Statistics { //TODO: Will implement IStatistics? why? why not?
    private final long receivedBytes;
    private final long sendBytes;

    public Statistics(long sendBytes, long receivedBytes) {
        this.sendBytes = sendBytes;
        this.receivedBytes = receivedBytes;
    }

    public long getReceivedBytes(){
        return receivedBytes;
    }

    public long getSendBytes(){
        return sendBytes;
    }
}
