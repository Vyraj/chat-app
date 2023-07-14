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
        final String rootPath = Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource("")).getPath();
        final String appConfigPath = rootPath + "application.properties";
        final Properties appProps = new Properties();
        appProps.load(new FileInputStream(appConfigPath));
        final int PORT = Integer.parseInt(appProps.getProperty("port"));
        final int CLIENTS_NUMBER = Integer.parseInt(appProps.getProperty("clients_number"));

        final ServerSocket serverSocket = new ServerSocket(PORT, CLIENTS_NUMBER);
        final ServerChat serverChat = new ServerChat(serverSocket);
        serverChat.startServer();
    }

}
