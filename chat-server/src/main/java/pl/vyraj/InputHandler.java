package pl.vyraj;

import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.OutputStreamWriter;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class InputHandler implements Runnable{
    protected static final List<InputHandler> INPUT_HANDLERS = new ArrayList<>();
    protected static final List<String> USER_NAMES = new ArrayList<>();
    private Socket socket;
    private BufferedReader input;
    private BufferedWriter output;
    private String clientUserName;

    public InputHandler(Socket socket) {
        try {
            this.socket = socket;
            this.output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientUserName = input.readLine();
            INPUT_HANDLERS.add(this);
            USER_NAMES.add(clientUserName);
            broadcastMessage("SERVER> " + clientUserName + " has started chatting!");
        } catch (IOException ioe) {
            closeConnection(socket, input, output);
        }
    }

    public void broadcastMessage(String messageToSend) {
        for (InputHandler inputHandler : INPUT_HANDLERS) {
            try {
                if (!inputHandler.clientUserName.equals(clientUserName)) {
                    inputHandler.output.write(messageToSend);
                    inputHandler.output.newLine();
                    inputHandler.output.flush();
                }
            } catch (IOException ioe) {
                closeConnection(socket, input, output);
            }
        }
    }

    public void broadcastMessageToOneClient(String messageToSend, String clientName) {
        for (InputHandler inputHandler : INPUT_HANDLERS) {
            try {
                if (inputHandler.clientUserName.equals(clientName)) {
                    inputHandler.output.write(messageToSend);
                    inputHandler.output.newLine();
                    inputHandler.output.flush();
                }
            } catch (IOException ioe) {
                closeConnection(socket, input, output);
            }
        }
    }

    public void removeClientHandler() {
        INPUT_HANDLERS.remove(this);
        USER_NAMES.remove(this.clientUserName);
        broadcastMessage("SERVER> " + clientUserName + " has left chatting!");
    }

    public void closeConnection(Socket socket, BufferedReader input, BufferedWriter output) {
        removeClientHandler();
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

    @Override
    public void run() {
        String messageFromClient = "";

        while (socket.isConnected()) {
            try {
                messageFromClient = input.readLine();
                if (messageFromClient.contains("@")) {
                    String[] words =  messageFromClient.split(" ");
                    String probablyUserName = words[1].replace("@", "");
                    String message = clientUserName + " " +  "PRIVATE> ";
                    for (int i = 0; i < words.length; i++) {
                        if (i > 1) {
                            message += words[i] + " ";
                        }
                    }
                    if (USER_NAMES.contains(probablyUserName)) {
                        broadcastMessageToOneClient(message, probablyUserName);
                    } else {
                        broadcastMessageToOneClient(probablyUserName + " probably is wrong or non-existing user name!!!", clientUserName);
                    }
                } else {
                    broadcastMessage(messageFromClient);
                }
            } catch (IOException ioe) {
                closeConnection(socket, input, output);
                break;
            }
        }
    }
}
