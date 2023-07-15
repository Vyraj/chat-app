package pl.vyraj;

import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.OutputStreamWriter;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.Socket;
import java.util.Objects;
import java.util.Properties;
import java.util.Scanner;

public class ClientChat {
    private Socket connection;
    private BufferedReader input;
    private BufferedWriter output;
    private String userName;

    public ClientChat(Socket connection, String userName) {
        try {
            this.connection = connection;
            this.output = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
            this.input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            this.userName = userName;
        } catch (IOException ioe) {
            closeConnection(connection, input, output);
        }
    }

    public void closeConnection(Socket socket, BufferedReader input, BufferedWriter output) {
        try {
            if (input != null) {
                input.close();
            }
            if (output != null) {
                output.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public Thread sendMessages() {
        final var messageSender = new MessageSender();
        return Thread.ofVirtual()
                .name("virtual-message-sender-", 0)
                .start(messageSender);
    }
    public Thread readMessages() {
        final var messageListener = new MessageListener();
        return Thread.ofVirtual()
                .name("virtual-message-listener-", 0)
                .start(messageListener);
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        final var rootPath = Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource("")).getPath();
        final var appConfigPath = rootPath + "application.properties";
        final var appProps = new Properties();
        appProps.load(new FileInputStream(appConfigPath));
        final var HOST = appProps.getProperty("host");
        final var PORT = Integer.parseInt(appProps.getProperty("port"));

        final var scanner = new Scanner(System.in);
        System.out.println("You have entered this chat application.\nGive me your name for the group chat: ");
        final var userName = scanner.nextLine();
        final var socket = new Socket(HOST, PORT);
        final var clientChat = new ClientChat(socket, userName);
        final var virtualChatListenerThread = clientChat.readMessages();
        final var virtualChatSenderThread = clientChat.sendMessages();
        virtualChatListenerThread.join();
        virtualChatSenderThread.join();

    }

    private class MessageSender implements Runnable {

        @Override
        public void run() {
            try {
                sendBufferedWriterMessage(userName);

                final var scanner = new Scanner(System.in);
                while (connection.isConnected()) {
                    var messageToSend = scanner.nextLine();
                    sendBufferedWriterMessage(userName + "> " + messageToSend);
                }
            } catch (IOException ioe) {
                closeConnection(connection, input, output);
            }
        }

        private synchronized void sendBufferedWriterMessage(String messageToSend) throws IOException {
            output.write(messageToSend);
            output.newLine();
            output.flush();
        }
    }

    private class MessageListener implements Runnable {

        @Override
        public void run() {
            var messageFromGroupChat = "";
            while (connection.isConnected()) {
                try {
                    messageFromGroupChat = input.readLine();
                    System.out.println(messageFromGroupChat);
                } catch (IOException ioe) {
                    closeConnection(connection, input, output);
                }
            }
        }
    }
}
