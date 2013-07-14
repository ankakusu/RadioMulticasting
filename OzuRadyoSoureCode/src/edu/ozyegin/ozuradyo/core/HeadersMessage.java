package edu.ozyegin.ozuradyo.core;

import java.util.List;

public final class HeadersMessage extends ClusterMessage {
    private static final long serialVersionUID = -4957662715665961205L;
    private final List<String> headers;

    public HeadersMessage(List<String> headers) {
        super(Type.HEADERS);
        this.headers = headers;
    }

    public List<String> getHeaders() {
        return headers;
    }
}
