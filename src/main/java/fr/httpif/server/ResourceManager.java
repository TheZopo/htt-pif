package fr.httpif.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import fr.httpif.server.enums.HttpMethodEnum;
import fr.httpif.server.exceptions.FileIsDirectoryException;
import fr.httpif.server.exceptions.ServerErrorException;
import fr.httpif.server.models.HttpRequest;
import fr.httpif.server.models.HttpResponse;

public class ResourceManager {
    private static final Logger logger = LoggerFactory.getLogger(ResourceManager.class);

    public static ResourceManager INSTANCE;
    private HttpResponse notImplementedResponse;
    private String webRoot;

    public ResourceManager(String webRoot) {
        INSTANCE = this;
        this.webRoot = webRoot;
        notImplementedResponse = new HttpResponse();
        notImplementedResponse.getHeaders().put("Content-Type", "text/plain");
        notImplementedResponse.setBody("Method not implemented.".getBytes());
    }

    public HttpResponse handleRequest(HttpRequest request) {
        HttpResponse response = notImplementedResponse;
        HttpMethodEnum method = request.getMethod();

        if(method == HttpMethodEnum.GET) response = handleGet(request);
        else if(method == HttpMethodEnum.POST) response = handlePost(request);
        else if(method == HttpMethodEnum.PUT) response = handlePut(request);
        else if(method == HttpMethodEnum.DELETE) response = handleDelete(request);
        else if(method == HttpMethodEnum.HEAD) response = handleHead(request);

        response.setVersion(request.getVersion());
        response.setStatusCode(200);
        response.getHeaders().put("Server", "htt-pif");
        if (response.getBody() != null) {
            response.getHeaders().put("Content-Length", String.valueOf(response.getBody().length));
        }

        return response;
    }

    private HttpResponse handleGet(HttpRequest request) {
        HttpResponse response = new HttpResponse();
        byte[] content = null;
        try {
            content = readResource(request.getUri());
        } catch (FileNotFoundException e) {
            response.setStatusCode(404);
        } catch (ServerErrorException e) {
            response.setStatusCode(500);
        } catch (FileIsDirectoryException e) {
            response.setStatusCode(403);
        }
        //TODO
        response.getHeaders().put("Content-Type", "text/html");
        response.setBody(content);

        logger.info("GET " + request.getUri() + " with status " + response.getStatusCode());

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

    private byte[] readResource(String uri) throws FileNotFoundException, FileIsDirectoryException, ServerErrorException {
        Path path = Path.of(webRoot, uri);
        byte[] content = null;

        File file = path.toFile();
        if(!file.exists()) throw new FileNotFoundException();
        if(file.isDirectory()) throw new FileIsDirectoryException();

        try {
            content = Files.readAllBytes(path);
        } catch(IOException e) {
            //TODO
            throw new ServerErrorException();
        }

        return content;
    }
}