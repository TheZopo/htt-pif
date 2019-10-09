package fr.httpif.server;

import fr.httpif.server.enums.HttpMethodEnum;
import fr.httpif.server.models.HttpRequest;
import fr.httpif.server.models.HttpResponse;

public enum ResourceManager {
    INSTANCE;

    private HttpResponse notImplementedResponse;

    ResourceManager() {
        notImplementedResponse = new HttpResponse();
        notImplementedResponse.getHeaders().put("Content-Type", "text/plain");
        notImplementedResponse.setBody("Method not implemented.");
    }

    public HttpResponse handleRequest(HttpRequest request) {
        HttpMethodEnum method = request.getMethod();

        if(method == HttpMethodEnum.GET) return handleGet(request);
        else if(method == HttpMethodEnum.POST) return handlePost(request);
        else if(method == HttpMethodEnum.PUT) return handlePut(request);
        else if(method == HttpMethodEnum.DELETE) return handleDelete(request);
        else if(method == HttpMethodEnum.HEAD) return handleHead(request);

        return null;
    }

    private HttpResponse handleGet(HttpRequest request) {
        return notImplementedResponse;
    }

    private HttpResponse handlePost(HttpRequest request) {
        return notImplementedResponse;
    }

    private HttpResponse handlePut(HttpRequest request) {
        return notImplementedResponse;
    }

    private HttpResponse handleDelete(HttpRequest request) {
        return notImplementedResponse;
    }

    private HttpResponse handleHead(HttpRequest request) {
        return notImplementedResponse;
    }
}
