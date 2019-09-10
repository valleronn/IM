package com.nc.controller;

import com.nc.model.message.Message;
import com.nc.model.message.MessageType;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * Represents ClientController
 */
public class ClientController {

    private final static Logger LOGGER = Logger.getLogger(MessageController.class);

    private Socket socket;
    private String serverName;
    private InputStream inputStream;
    private OutputStream outputStream;
    private BufferedReader bufferedIn;
    private int serverPort;
    private MessageController messageController;
    private List<MessageListener> messageListeners = new ArrayList<>();
    private List<UserStatusListener> userStatusListeners = new ArrayList<>();


    public ClientController(String serverName, int serverPort) {
        this.serverName = serverName;
        this.serverPort = serverPort;
        messageController = new MessageController();
    }

    /**
     * Connects to a server
     * @return returns true or false
     */
    public boolean connect() {
        try {
            this.socket = new Socket(serverName, serverPort);
            System.out.println("Client port is " + socket.getLocalPort());
            this.inputStream = socket.getInputStream();
            this.outputStream = socket.getOutputStream();
            this.bufferedIn = new BufferedReader(new InputStreamReader(inputStream));
            return true;
        } catch (IOException e) {
            LOGGER.error("Connection to server error: ", e);
        }
        return false;
    }

    /**
     * Sends new message
     * @param sendTo recipient
     * @param sentFrom sender
     * @param msgBody message body
     * @throws IOException
     */
    public void sendChatMessage(String sendTo, String sentFrom, String msgBody) throws IOException {
        Message chatMessage = new Message();
        chatMessage.setType(MessageType.MSG);
        chatMessage.setTo(sendTo);
        chatMessage.setFrom(sentFrom);
        chatMessage.setBody(msgBody);
        outputStream.write(messageController.createMessage(chatMessage).getBytes());
    }

    /**
     * Registers new users, sends register message
     * @param login login
     * @param password password
     * @return true or false
     * @throws IOException
     */
    public boolean register(String login, String password) throws IOException {
        boolean result = false;
        Message registerMessage = new Message();
        registerMessage.setType(MessageType.REGISTER);
        registerMessage.setFrom(login);
        registerMessage.setBody(password);
        String cmd = messageController.createMessage(registerMessage);
        outputStream.write(cmd.getBytes());

        String response = messageController.extractMessage(bufferedIn.readLine()).getStatus();
        System.out.println("Response line: " + response);
        if ("Registration successful".equalsIgnoreCase(response)) {
            result = true;
        }
        return result;
    }

    /**
     * Sends login message
     * @param login login
     * @param password password
     * @return returns true or false
     * @throws IOException
     */
    public boolean login(String login, String password) throws IOException {
        boolean result = false;
        Message loginMessage = new Message();
        loginMessage.setType(MessageType.LOGIN);
        loginMessage.setFrom(login);
        loginMessage.setBody(password);
        String cmd = messageController.createMessage(loginMessage);
        outputStream.write(cmd.getBytes());

        String response = messageController.extractMessage(bufferedIn.readLine()).getStatus();
        System.out.println("Response line: " + response);
        if ("Login successful".equalsIgnoreCase(response)) {
            result = true;
        }
        return result;
    }

    public boolean updateProfile(String login, String newLogin, String password) throws IOException {
        boolean result = false;
        Message updateProfileMessage = new Message();
        updateProfileMessage.setType(MessageType.UPDATEPROFILE);
        updateProfileMessage.setFrom(login);
        updateProfileMessage.setTo(newLogin);
        updateProfileMessage.setBody(password);
        String cmd = messageController.createMessage(updateProfileMessage);
        outputStream.write(cmd.getBytes());

        String response = messageController.extractMessage(bufferedIn.readLine()).getStatus();
        System.out.println("Response line: " + response);
        if ("Profile updated".equalsIgnoreCase(response)) {
            result = true;
        }
        return result;
    }

    /**
     * Sends logoff message
     * @throws IOException
     */
    public void logoff() throws IOException {
        Message logoffMessage = new Message();
        logoffMessage.setType(MessageType.LOGOFF);
        String cmd = messageController.createMessage(logoffMessage);
        outputStream.write(cmd.getBytes());
    }

    /**
     * Sends JOINGROUPCHAT message
     * @throws IOException
     */
    public void joinGroupChat(String chatName) throws IOException {
        Message joinGroupChatMessage = new Message();
        joinGroupChatMessage.setType(MessageType.JOINGROUPCHAT);
        joinGroupChatMessage.setBody(chatName);
        String cmd = messageController.createMessage(joinGroupChatMessage);
        outputStream.write(cmd.getBytes());
    }

    /**
     * Sends LEAVEGROUPCHAT message
     * @throws IOException
     */
    public void leaveGroupChat(String chatName) throws IOException {
        Message leaveGroupChatMessage = new Message();
        leaveGroupChatMessage.setType(MessageType.LEAVEGROUPCHAT);
        leaveGroupChatMessage.setBody(chatName);
        String cmd = messageController.createMessage(leaveGroupChatMessage);
        outputStream.write(cmd.getBytes());
    }

    /**
     * Launches a new thread to read messages from server.
     */
    public void messageReader() {
        Thread t = new Thread() {
            @Override
            public void run() {
                readMessage();
            }
        };
        t.start();
    }

    /**
     * Reads incoming messages from server
     */
    private void readMessage() {
        try {
            String line;
            while ((line = bufferedIn.readLine()) != null) {
                String msgType = messageController.extractMessage(line).getType().toString();
                String msgStatus = messageController.extractMessage(line).getStatus();
                Message message = messageController.extractMessage(line);
                if (message != null) {
                    String cmd = msgType;
                    if ("online".equalsIgnoreCase(msgStatus)) {
                        handleOnline(message);
                    } else if ("offline".equalsIgnoreCase(msgStatus)) {
                        handleOffline(message);
                    } else if ("msg".equalsIgnoreCase(cmd)) {
                        handleMessage(message);
                    }
                }
            }
        } catch (Exception ex) {
            LOGGER.error("ReadMessage exception: ", ex);
            try {
                socket.close();
            } catch (IOException e) {
                LOGGER.error("ReadMessage (I/O): ", e);
            }
        }
    }

    private synchronized void handleOnline(Message message) {
        String login = message.getFrom();
        for(UserStatusListener listener: userStatusListeners) {
            listener.online(login);
        }
    }

    private synchronized void handleOffline(Message message) {
        String login = message.getFrom();
        for(UserStatusListener listener: userStatusListeners) {
            listener.offline(login);
        }
    }

    private synchronized void handleMessage(Message message) {
        String from = message.getFrom();
        String body = message.getBody();
        for (MessageListener listener: messageListeners) {
            listener.onMessage(from, body);
        }
    }

    public synchronized void addUserStatusListener(UserStatusListener listener) {
        userStatusListeners.add(listener);
    }

    public synchronized void removeUserStatusListener(UserStatusListener listener) {
        userStatusListeners.remove(listener);
    }

    public synchronized void addMessageListener(MessageListener listener) {
        messageListeners.add(listener);
    }

    public synchronized void removeMessageListener(MessageListener listener) {
        messageListeners.remove(listener);
    }
}
