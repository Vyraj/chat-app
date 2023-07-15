package pl.vyraj;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Properties;

public class ServerChat {
    private final ServerSocket server;

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
        final var rootPath = Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource("")).getPath();
        final var appConfigPath = rootPath + "application.properties";
        final var appProps = new Properties();
        appProps.load(new FileInputStream(appConfigPath));
        final var PORT = Integer.parseInt(appProps.getProperty("port"));
        final var CLIENTS_NUMBER = Integer.parseInt(appProps.getProperty("clients_number"));

        final var serverSocket = new ServerSocket(PORT, CLIENTS_NUMBER);
        final var serverChat = new ServerChat(serverSocket);
        serverChat.startServer();
    }

}
