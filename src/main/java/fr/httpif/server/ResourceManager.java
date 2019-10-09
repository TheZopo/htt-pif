package fr.httpif.server;

import fr.httpif.server.exceptions.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URLConnection;
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

    public HttpResponse handleRequest(HttpRequest request) throws BadRequestException, ServerErrorException {
        HttpResponse response = notImplementedResponse;
        HttpMethodEnum method = request.getMethod();

        if(method == HttpMethodEnum.GET) response = handleGet(request);
        else if(method == HttpMethodEnum.POST) response = handlePost(request);
        else if(method == HttpMethodEnum.PUT) response = handlePut(request);
        else if(method == HttpMethodEnum.DELETE) response = handleDelete(request);
        else if(method == HttpMethodEnum.HEAD) response = handleHead(request);

        response.setVersion(request.getVersion());
        response.getHeaders().put("Server", "htt-pif");

        if (response.getBody() != null) {
            response.getHeaders().put("Content-Length", String.valueOf(response.getBody().length));
        }

        return response;
    }

    private HttpResponse handleGet(HttpRequest request) {
        HttpResponse response = new HttpResponse();
        byte[] content = null;
        String mimeType = null;

        try {
            content = readResource(request.getUri());
            mimeType = getResourceMimeType(request.getUri());
        } catch (FileNotFoundException e) {
            response.setStatusCode(404);
        } catch (ServerErrorException e) {
            response.setStatusCode(500);
        } catch (FileIsDirectoryException e) {
            response.setStatusCode(403);
        }

        if(mimeType != null) response.getHeaders().put("Content-Type", mimeType);
        response.setBody(content);

        logger.info("GET " + request.getUri() + " with status " + response.getStatusCode());

        return response;
    }

    private HttpResponse handlePost(HttpRequest request) {
        // TODO dynamic resource
        return handleGet(request);
    }

    private HttpResponse handlePut(HttpRequest request) throws ServerErrorException, BadRequestException {
        Path path = Path.of(webRoot, request.getUri());
        File file = path.toFile();

        if (request.getBody() != null) {
            throw new BadRequestException("Empty body");
        }

        try {
            Files.write(path, request.getBody());
        } catch(IOException e) {
            logger.error(e.toString());
            throw new ServerErrorException();
        }
        HttpResponse response = new HttpResponse();
        response.getHeaders().put("Content-Location", request.getUri());
        response.setStatusCode(201); //created
        return response;
    }

    private HttpResponse handleDelete(HttpRequest request) throws ServerErrorException {
        HttpResponse response = new HttpResponse();
        Path path = Path.of(webRoot, request.getUri());
        File file = path.toFile();

        if (!file.exists()) {
            response.setStatusCode(404);
            return response;
        }
        try {
            Files.delete(path);
        } catch(IOException e) {
            logger.error(e.toString());
            throw new ServerErrorException();
        }

        response.setStatusCode(204);
        return response;
    }

    private HttpResponse handleHead(HttpRequest request) {
        HttpResponse response = handleGet(request);
        response.setBody(null);
        return response;
    }

    private byte[] readResource(String uri) throws FileNotFoundException, FileIsDirectoryException, ServerErrorException {
        Path path = Path.of(webRoot, uri);
        byte[] content;

        File file = path.toFile();
        if(!file.exists()) throw new FileNotFoundException();
        if(file.isDirectory()) throw new FileIsDirectoryException();

        try {
            content = Files.readAllBytes(path);
        } catch(IOException e) {
            logger.error(e.toString());
            throw new ServerErrorException();
        }

        return content;
    }

    private String getResourceMimeType(String uri) {
        return URLConnection.guessContentTypeFromName(uri);
    }
}