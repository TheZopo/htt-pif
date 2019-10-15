package fr.httpif.server.network;

import fr.httpif.server.exceptions.ServerErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.httpif.server.ResourceManager;
import fr.httpif.server.enums.HttpMethodEnum;
import fr.httpif.server.exceptions.BadRequestException;
import fr.httpif.server.models.HttpRequest;
import fr.httpif.server.models.HttpResponse;

import java.io.*;
import java.net.Socket;

public class ClientThread extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(ClientThread.class);
    private Socket socket;

    @Override
    public void run() {
        logger.info("Connection, sending data.");
        BufferedReader in = null;
        HttpResponse response = null;
        try {
            in = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));

            HttpRequest request = parseRequest(in);
            response = ResourceManager.INSTANCE.handleRequest(request);

        } catch (BadRequestException ex) {
            logger.warn("BadRequest " + ex.toString());
            response = new HttpResponse();
            response.setStatusCode(400);
        } catch (Exception e) {
            logger.error(e.toString());
            e.printStackTrace();
            response = new HttpResponse();
            response.setStatusCode(500);
        }

        try {
            if (response != null) {
                OutputStream os = socket.getOutputStream();
                os.write(response.toBytes());
                os.flush();
            }
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public ClientThread(Socket socket) {
        this.socket = socket;
    }

    private HttpRequest parseRequest(BufferedReader reader) throws BadRequestException {
        HttpRequest request = new HttpRequest();
        try {
            String requestLine = reader.readLine();
            String[] splittedRequestLine = requestLine.split(" ");
            HttpMethodEnum method = HttpMethodEnum.valueOf(splittedRequestLine[0]);
            request.setMethod(method);
            request.setUri(splittedRequestLine[1]);
            request.setVersion(splittedRequestLine[2]);

            String headerLine = reader.readLine();
            while (!"".equals(headerLine)) {
                String[] splittedHeaderLine = headerLine.split(":");
                request.getHeaders().put(splittedHeaderLine[0].trim(), splittedHeaderLine[1].trim());
                headerLine = reader.readLine();
            }

            byte body[] = null;
            if(request.getHeaders().containsKey("Content-Length")) {
                int bodyLength = Integer.parseInt(request.getHeaders().get("Content-Length"));
                char charBody[] = new char[bodyLength];
                reader.read(charBody, 0, bodyLength);
                body = new String(charBody).getBytes();
            }
            request.setBody(body);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            throw new BadRequestException(ex.toString());
        }

        return request;
    }
}
