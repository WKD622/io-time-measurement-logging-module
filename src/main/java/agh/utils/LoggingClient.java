package agh.utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class LoggingClient implements Runnable {

    private String host;
    private int port;
    private Socket socket;
    private PrintWriter out;

    public static void main(String args[]) {
        String host = "127.0.0.1";
        int port = 8081;
        new LoggingClient(host, port);
    }

    public LoggingClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void run(){
        try {
            System.out.println("Connecting to host " + host + " on port " + port + ".");

            try {
                socket = new Socket(host, 8081);
                out = new PrintWriter(socket.getOutputStream(), true);
            } catch (UnknownHostException e) {
                System.err.println("Unknown host: " + host);
                System.exit(1);
            } catch (IOException e) {
                System.err.println("Unable to get streams from server");
                System.exit(1);
            }
            try {
                while (true) {
                    Thread.sleep(5000);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            out.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

