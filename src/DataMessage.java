public final class DataMessage extends ClusterMessage {
    private static final long serialVersionUID = 7495709832490122254L;
    private final byte[] data;

    public DataMessage(byte[] data) {
        super(Type.DATA);
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }
}
