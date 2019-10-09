///A Simple Web Server (WebServer.java)

package fr.httpif.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.httpif.server.network.ClientThread;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Example program from Chapter 1 Programming Spiders, Bots and Aggregators in
 * Java Copyright 2001 by Jeff Heaton
 * 
 * WebServer is a very simple web-server. Any request is responded with a very
 * simple web-page.
 * 
 * @author Jeff Heaton
 * @version 1.0
 */
public class WebServer {
  private static final Logger logger = LoggerFactory.getLogger(WebServer.class);
  private ResourceManager resourceManager;

  public WebServer(String[] args) {
    if (args.length > 0) {
      this.resourceManager = new ResourceManager(args[0]);
    }
    else {
      this.resourceManager = new ResourceManager("../../web_root");
    }
  }

  /**
   * WebServer constructor.
   */
  protected void start() {
    ServerSocket s;

    logger.info("Webserver starting up on port 80");
    logger.info("(press ctrl-c to exit)");
    try {
      // create the main server socket
      s = new ServerSocket(80);
    } catch (Exception e) {
      logger.error("Error: " + e);
      return;
    }

    logger.info("Waiting for connection");
    for (;;) {
      try {
        // wait for a connection
        Socket remote = s.accept();
        new ClientThread(remote).start();

      } catch (Exception e) {
        logger.error("Error: " + e);
      }
    }
  }

  /**
   * Start the application.
   * 
   * @param args
   *            Command line parameters are not used.
   */
  public static void main(String args[]) {
    WebServer ws = new WebServer(args);
    ws.start();
  }
}
