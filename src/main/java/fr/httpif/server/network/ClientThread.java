package fr.httpif.server.network;

import fr.httpif.server.ResourceManager;
import fr.httpif.server.enums.HttpMethodEnum;
import fr.httpif.server.exceptions.BadRequestException;
import fr.httpif.server.models.HttpRequest;
import fr.httpif.server.models.HttpResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientThread extends Thread {
    private Socket socket;

    @Override
    public void run() {
        System.out.println("Connection, sending data.");
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));

            HttpRequest request = parseRequest(in);
            HttpResponse response = ResourceManager.INSTANCE.handleRequest(request);
            if (response != null) {
                PrintWriter out = new PrintWriter(socket.getOutputStream());
                out.write(response.toString());
                out.flush();
            }
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BadRequestException ex) {
            //TODO send error 400
            ex.printStackTrace();
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
        try {
            HttpRequest request = new HttpRequest();

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

            String body = "";
            String bodyLine = reader.readLine();
            while (!"".equals(bodyLine)) {
                body += bodyLine;
                bodyLine = reader.readLine();
            }
            request.setBody(body);
            return request;
        }
        catch (Exception ex) {
            throw new BadRequestException(ex.toString());
        } finally {
            return null;
        }

    }
}
