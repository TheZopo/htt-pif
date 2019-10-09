package fr.httpif.server.models;

import fr.httpif.server.enums.HttpMethodEnum;

public class HttpRequest extends HttpMessage {
    private HttpMethodEnum method;

    private String uri;

    public HttpMethodEnum getMethod() {
        return method;
    }

    public void setMethod(HttpMethodEnum method) {
        this.method = method;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
