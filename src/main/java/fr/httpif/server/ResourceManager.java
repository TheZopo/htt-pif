package fr.httpif.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import fr.httpif.server.enums.HttpMethodEnum;
import fr.httpif.server.models.HttpRequest;
import fr.httpif.server.models.HttpResponse;

public class ResourceManager {
    private HttpResponse notImplementedResponse;
    private String webRoot;

    public ResourceManager(String webRoot) {
        this.webRoot = webRoot;

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
        String content = "";
        try {
            content = readResource(request.getUri());
        } catch (FileNotFoundException e) {
            //TODO: 404 error
            e.printStackTrace();
        }

        HttpResponse response = new HttpResponse();
        response.getHeaders().put("Content-Type", "text/html");
        response.setBody(content);

        return response;
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

    private String readResource(String uri) throws FileNotFoundException {
        String path = webRoot + uri;
        String content = "";

        File file = new File(path);
        if(!file.exists()) throw new FileNotFoundException();

        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);

        try {
            String line;
            while((line = br.readLine()) != null) {
                content += line + "\n";
            }
        } catch(IOException e) {
            //TODO: handle ? log ?
        }

        return content;
    }
}