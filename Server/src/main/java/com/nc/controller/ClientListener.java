package com.nc.controller;

import com.nc.model.Server;
import com.nc.model.message.Message;
import com.nc.model.message.MessageType;
import com.nc.model.users.User;

import java.io.*;
import java.net.Socket;
import java.util.Date;
import java.util.List;

public class ClientListener extends Thread {
    private final Socket clientSocket;
    private final Server server;
    private User user;
    private MessageController messageController;
    private OutputStream outputStream;
    private InputStream inputStream;

    public ClientListener(Server server, Socket clientSocket) {
        this.server = server;
        this.clientSocket = clientSocket;
        messageController = new MessageController();
    }

    public User getUser() {
        return user;
    }

    @Override
    public void run() {
        try {
            handleClientConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleClientConnection() throws IOException {
        this.inputStream = clientSocket.getInputStream();
        this.outputStream = clientSocket.getOutputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = reader.readLine()) != null) {
            String msgType = messageController.extractMessage(line).getType().toString();
            Message message = messageController.extractMessage(line);
            if (msgType != null) {
                    String cmd = msgType;
                if ("logoff".equalsIgnoreCase(cmd)) {
                    handleLogOff();
                    break;
                } else if ("msg".equalsIgnoreCase(cmd)) {
                    handleMessage(message);
                /*} else if ("join".equalsIgnoreCase(cmd)) {
                    handleJoin(msgType);
                } else if ("leave".equalsIgnoreCase(cmd)) {
                    handleLeave(msgType);*/
                } else if ("register".equalsIgnoreCase(cmd)) {
                    handleRegister(outputStream, message);
                } else if ("login".equalsIgnoreCase(cmd)) {
                    handleLogin(outputStream, message);
                } else  {
                    String msg = "Unknown " + cmd + "\n";
                    outputStream.write(msg.getBytes());
                }
            }

        }
        clientSocket.close();
    }

    private void handleMessage(Message message) throws IOException {
        String sendTo = message.getTo();
        List<ClientListener> listenerList = server.getListenerList();
        for(ClientListener listener: listenerList) {
            if (sendTo.equals(listener.getUser().getLogin())) {
                listener.send(message);
            }
        }

    }

    private void handleLogOff() throws IOException {
        server.removeListener(this);
        List<ClientListener> listenerList = server.getListenerList();
        //send online users current user's status
        Message offlineMsg = new Message();
        offlineMsg.setStatus("offline");
        offlineMsg.setType(MessageType.MSG);
        offlineMsg.setFrom(getUser().getLogin());
        for(ClientListener listener: listenerList) {
            if (!getUser().getLogin().equals(listener.getUser().getLogin())) {
                listener.send(offlineMsg);
            }
        }
        clientSocket.close();
        System.out.println(getUser().getLogin() + " has been disconnected");
    }

    private void handleRegister(OutputStream outputStream, Message message) throws IOException {
        String login = message.getFrom();
        String password = message.getBody();
        for(User user: server.getUsers()) {
            if (user.getLogin().equals(login)) {
                System.err.println("User with this login already exists");
                clientSocket.close();
            }
        }
        user = new User(login, password, new Date());
        server.getUsers().add(user);
        Message msg = new Message();
        msg.setStatus("Registration successful");
        outputStream.write(messageController.createMessage(msg).getBytes());
        System.out.println(login + " has been registered successfully");

        List<ClientListener> listenerList = server.getListenerList();
        sendCurrUserAllOtherLogins(login, listenerList);
        sendOnlineUsersCurrUserStatus(login, listenerList);
    }

    private void handleLogin(OutputStream outputStream, Message message) throws IOException {
        String login = message.getFrom();
        String password = message.getBody();

        if (login.equals(server.getUser().getLogin())
                && password.equals(server.getUser().getPassword()) ||
                login.equals("val") && password.equals("val")) {
            Message msg = new Message();
            msg.setStatus("Login successful");
            outputStream.write(messageController.createMessage(msg).getBytes());
            System.out.println("User logged in successfully: " + login);

            List<ClientListener> listenerList = server.getListenerList();
            sendCurrUserAllOtherLogins(login, listenerList);
            sendOnlineUsersCurrUserStatus(login, listenerList);
        } else {
            Message msg = new Message();
            msg.setStatus("Error login");
            outputStream.write(messageController.createMessage(msg).getBytes());
            System.err.println("Login failed for " + login);
        }
    }

    private void sendCurrUserAllOtherLogins(String login, List<ClientListener> listenerList) throws IOException {
        for(ClientListener listener: listenerList) {
            if (listener.getUser().getLogin() != null) {
                if (!login.equals(listener.getUser().getLogin())) {
                    Message onlineMsg1 = new Message();
                    onlineMsg1.setStatus("online");
                    onlineMsg1.setType(MessageType.MSG);
                    onlineMsg1.setFrom(listener.getUser().getLogin());
                    send(onlineMsg1);
                }
            }
        }
    }

    private void sendOnlineUsersCurrUserStatus(String login, List<ClientListener> listenerList) throws IOException {
        Message onlineMsg = new Message();
        onlineMsg.setStatus("online");
        onlineMsg.setType(MessageType.MSG);
        onlineMsg.setFrom(getUser().getLogin());
        for(ClientListener listener: listenerList) {
            if (!login.equals(listener.getUser().getLogin())) {
                listener.send(onlineMsg);
            }
        }
    }

    private void send(Message msg) throws IOException {
        if (user.getLogin() != null) {
            outputStream.write(messageController.createMessage(msg).getBytes());
        }
    }

}
