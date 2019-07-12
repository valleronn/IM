package com.nc.controller;

import com.nc.model.message.Message;
import com.nc.model.message.MessageType;
import com.nc.model.users.User;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ClientController {
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



    public void run() throws IOException {
        if (!connect()) {
            System.err.println("Connect failed");
        } else {
            System.out.println("Connect successful");
            /*if (login("guest", "guest")) {
                System.out.println("Login successful");
                //client.msg("val", "hello world");
            } else {
                System.err.println("Login failed");
            }*/
            //client.logoff();
        }
    }

    public boolean connect() {
        try {
            this.socket = new Socket(serverName, serverPort);
            System.out.println("Client port is " + socket.getLocalPort());
            this.inputStream = socket.getInputStream();
            this.outputStream = socket.getOutputStream();
            this.bufferedIn = new BufferedReader(new InputStreamReader(inputStream));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void sendChatMessage(String sendTo, String msgBody) throws IOException {
        Message chatMessage = new Message();
        chatMessage.setType(MessageType.MSG);
        chatMessage.setTo(sendTo);
        chatMessage.setBody(msgBody);
        outputStream.write(messageController.createMessage(chatMessage).getBytes());
    }

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
            //messageReader();
            result = true;
        }
        return result;
    }

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
            messageReader();
            result = true;
        }
        //socket.close();
        return result;
    }

    public void messageReader() {
        Thread t = new Thread() {
            @Override
            public void run() {
                readMessage();
            }
        };
        t.start();
    }

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
            ex.printStackTrace();
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleOnline(Message message) {
        String login = message.getFrom();
        for(UserStatusListener listener: userStatusListeners) {
            listener.online(new User(login, null, new Date()));
        }
    }

    private void handleOffline(Message message) {
        String login = message.getFrom();
        for(UserStatusListener listener: userStatusListeners) {
            listener.offline(new User(login, null, new Date()));
        }
    }

    private void handleMessage(Message message) {
        String from = message.getFrom();
        String body = message.getBody();
        for (MessageListener listener: messageListeners) {
            listener.onMessage(from, body);
        }
    }

    public void addUserStatusListener(UserStatusListener listener) {
        userStatusListeners.add(listener);
    }

    public void removeUserStatusListener(UserStatusListener listener) {
        userStatusListeners.remove(listener);
    }

    public void addMessageListener(MessageListener listener) {
        messageListeners.add(listener);
    }

    public void removeMessageListener(MessageListener listener) {
        messageListeners.remove(listener);
    }
}
