package pl.vyraj;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerChat {
    private final ServerSocket server;
    private static final int PORT = 7777;
    private static final int CLIENTS_NUMBER = 10;

    public ServerChat(ServerSocket server) {
        this.server = server;
    }

    public void startServer() {
        try {
            while (!server.isClosed()) {
                Socket socket = server.accept();
                System.out.println("We have new client connected!");
                InputHandler inputHandler = new InputHandler(socket);

                Thread thread = new Thread(inputHandler);
                thread.start();
            }
        } catch (IOException ioe) {
            closeServer();
        }
    }

    public void closeServer() {
        try {
            if (server != null) {
                server.close();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT, CLIENTS_NUMBER);
        ServerChat serverChat = new ServerChat(serverSocket);
        serverChat.startServer();
    }

}
