package fr.httpif.server.models;

public class HttpResponse extends HttpMessage {
    @Override
    public String toString() {
        String output = "HTTP/1.0 200 OK\n";

        for(String key : headers.keySet()) {
            output += key + ": " + headers.get(key) + "\n";
        }

        output += "Server: htt-pif\n";
        output += "\n"; //end of the header
        output += body;

        return output;
    }
}
