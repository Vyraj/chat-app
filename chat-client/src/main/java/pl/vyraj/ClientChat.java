package pl.vyraj;

import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.OutputStreamWriter;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class ClientChat {
    private Socket connection;
    private BufferedReader input;
    private BufferedWriter output;
    private String userName;
    private static final int PORT = 7777;
    private static final String HOST = "localhost";

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

    public void sendMessages() {
        MessageSender messageSender = new MessageSender();
        Thread thread = new Thread(messageSender);
        thread.start();
    }
    public void readMessages() {
        MessageListener messageListener = new MessageListener();
        Thread thread = new Thread(messageListener);
        thread.start();
    }

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("You have entered this chat application.\nGive me your name for the group chat: ");
        String userName = scanner.nextLine();
        Socket socket = new Socket(HOST, PORT);
        ClientChat clientChat = new ClientChat(socket, userName);
        clientChat.readMessages();
        clientChat.sendMessages();
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
