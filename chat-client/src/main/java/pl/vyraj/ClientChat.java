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
        MessageSender messageSender = new MessageSender();
        return Thread.ofVirtual()
                .name("virtual-message-sender-", 0)
                .start(messageSender);
    }
    public Thread readMessages() {
        MessageListener messageListener = new MessageListener();
        return Thread.ofVirtual()
                .name("virtual-message-listener-", 0)
                .start(messageListener);
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        final String rootPath = Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource("")).getPath();
        final String appConfigPath = rootPath + "application.properties";
        final Properties appProps = new Properties();
        appProps.load(new FileInputStream(appConfigPath));
        final String HOST = appProps.getProperty("host");
        final int PORT = Integer.parseInt(appProps.getProperty("port"));

        final Scanner scanner = new Scanner(System.in);
        System.out.println("You have entered this chat application.\nGive me your name for the group chat: ");
        final String userName = scanner.nextLine();
        final Socket socket = new Socket(HOST, PORT);
        final ClientChat clientChat = new ClientChat(socket, userName);
        final Thread virtualChatListenerThread = clientChat.readMessages();
        final Thread virtualChatSenderThread = clientChat.sendMessages();
        virtualChatListenerThread.join();
        virtualChatSenderThread.join();

    }

    private class MessageSender implements Runnable {

        @Override
        public void run() {
            try {
                sendBufferedWriterMessage(userName);

                Scanner scanner = new Scanner(System.in);
                while (connection.isConnected()) {
                    String messageToSend = scanner.nextLine();
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
            String messageFromGroupChat = "";
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
