package agh.utils;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class LoggingServer implements Runnable {
    public static final int PORT_NUMBER = 8081;

    protected Socket socket;

    private LoggingServer(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        InputStream in = null;
        try {
            in = socket.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String request;
            while ((request = br.readLine()) != null) {
                System.out.println("Message received:" + request);
            }
        } catch (IOException ex) {
            System.out.println("Unable to get streams from client");
        } finally {
            try {
                in.close();
                socket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("Logging server start");
        ServerSocket server = null;
        try {
            server = new ServerSocket(PORT_NUMBER);
            while (true) {
                new LoggingServer(server.accept());
            }
        } catch (IOException ex) {
            System.out.println("Unable to start server");
        } finally {
            try {
                if (server != null)
                    server.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
