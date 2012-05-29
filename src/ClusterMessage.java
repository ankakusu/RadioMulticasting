import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public abstract class ClusterMessage implements Serializable {
    private static final long serialVersionUID = 4994179451984895518L;

    public static enum Type {
        DATA,
        HEADERS,
        JOIN
    }

    protected final Type type;

    protected ClusterMessage(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public byte[] pack() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream(bos);   
        out.writeObject(this);
        return bos.toByteArray();
    }

    public static ClusterMessage unpack(byte[] bytes)
            throws IOException, ClassNotFoundException {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInput in = new ObjectInputStream(bis);
        return (ClusterMessage) in.readObject();
    }
}
