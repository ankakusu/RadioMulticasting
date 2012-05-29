
public class JoinMessage extends ClusterMessage {
    private static final long serialVersionUID = 7495709832490122255L;

    public JoinMessage() {
        super(Type.JOIN);
    }
}
