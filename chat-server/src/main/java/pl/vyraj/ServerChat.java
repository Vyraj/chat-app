package pl.vyraj;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
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
                var socket = server.accept();
                System.out.println("We have new client connected!");
                var virtualChatInputProcessorThread = processInput(socket);
            }
        } catch (IOException ioe) {
            closeServer();
        }
    }

    private Thread processInput(Socket socket) {
        final var inputHandler = new InputHandler(socket);
        return Thread.ofVirtual()
                .name("virtual-input-processor-", 0)
                .start(inputHandler);
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
