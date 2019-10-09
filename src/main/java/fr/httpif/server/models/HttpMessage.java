package fr.httpif.server.models;

import java.util.HashMap;

public abstract class HttpMessage {
    protected HashMap<String, String> headers;
    protected byte[] body;
    protected String version;

    public HttpMessage() {
        headers = new HashMap<>();
    }

    public HashMap<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(HashMap<String, String> headers) {
        this.headers = headers;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
