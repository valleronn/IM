package com.nc.controller;

import com.nc.model.IOworker;
import com.nc.model.message.Message;
import com.nc.model.message.MessageType;
import com.nc.model.users.Admin;
import com.nc.model.users.ChatRoom;
import com.nc.model.users.User;

import java.io.*;
import java.net.Socket;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;

public class ClientListener extends Thread {

    private final static Logger LOGGER = Logger.getLogger(ClientListener.class);
    private static final File USERS_DATA = new File("users.bin");

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
            LOGGER.error("handleClientConnection error: ", e);
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                LOGGER.error("Fails to close clientSocket properly: ", e);
            }
        }
    }

    private void handleClientConnection() throws IOException {
        this.inputStream = clientSocket.getInputStream();
        this.outputStream = clientSocket.getOutputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        logoffhappened: while ((line = reader.readLine()) != null) {
            String msgType = messageController.extractMessage(line).getType().toString();
            Message message = messageController.extractMessage(line);
            if (msgType != null) {
                switch (msgType) {
                    case "LOGOFF":
                        handleLogOff();
                        break logoffhappened;
                    case "MSG":
                        handleMessage(message);
                        break;
                    case "JOINGROUPCHAT":
                        handleJoinGroupChat(message);
                        break;
                    case "LEAVEGROUPCHAT":
                        handleLeaveGroupChat(message);
                        break;
                    case "REGISTER":
                        handleRegister(outputStream, message);
                        break;
                    case "LOGIN":
                        handleLogin(outputStream, message);
                        break;
                    case "UPDATEPROFILE":
                        handleUpdateProfile(outputStream, message);
                        break;
                    case "INVITEUSERTOCHAT":
                        handleInviteUserToChat(message);
                        break;
                    case "INVITEUSERSTOGROUPCHAT":
                        handleInviteUserToGroupChat(message);
                        break;
                    case "REMOVEUSERFROMGROUPCHAT":
                        handleRemoveUserFromGroupChat(message);
                        break;
                    case "ADDTOBANLIST":
                        handleBanUser(message);
                        break;
                    case "REMOVEFROMBANLIST":
                        handleUnBanUser(message);
                        break;
                    default:
                        String msg = "Unknown " + msgType + "\n";
                        outputStream.write(msg.getBytes());
                }
            }
        }
    }

    private void handleLeaveGroupChat(Message message) {
        for(ChatRoom chatRoom: server.getChatRooms()) {
            if (chatRoom.getChatName().equals(message.getBody())) {
                server.getChatRooms().remove(chatRoom);
            }
        }
    }

    private void handleJoinGroupChat(Message message) {
        String chatRoomName = message.getBody();
        if (server.chatExists(chatRoomName)) {
            ChatRoom chatRoom = server.getChatRoomByName(chatRoomName);
            chatRoom.addUser(this.getUser());
        } else {
            ChatRoom chatRoom = new ChatRoom();
            chatRoom.setChatName(message.getBody());
            chatRoom.addUser(this.getUser());
            server.getChatRooms().add(chatRoom);
        }
    }

    private void handleInviteUserToChat(Message message) throws IOException {
        String sender = message.getFrom();
        String recipient = message.getTo();
        Message inviteMessage = new Message();
        inviteMessage.setType(MessageType.INVITEUSERTOCHAT);
        inviteMessage.setStatus("invitation");
        inviteMessage.setFrom(sender);
        inviteMessage.setTo(recipient);
        for(ClientListener listener: server.getListenerList()) {
            if (listener.getUser().getLogin().equals(recipient)) {
                listener.send(inviteMessage);
            }
        }
    }

    private void handleInviteUserToGroupChat(Message message) throws IOException {
        String sender = message.getFrom();
        String recipients = message.getTo();
        Message inviteMessage = new Message();
        inviteMessage.setType(MessageType.INVITEUSERSTOGROUPCHAT);
        inviteMessage.setStatus("invitation");
        inviteMessage.setFrom(sender);
        inviteMessage.setTo(recipients);

        String[] recipientsArr = recipients.split("\\;");
        for(String login: recipientsArr) {
            for(ClientListener listener: server.getListenerList()) {
                String currUser = listener.getUser().getLogin();
                if (currUser.equals(login)
                    && !server.getChatRoomByName(sender).containsUser(currUser)) {
                    listener.send(inviteMessage);
                }
            }
        }
    }

    private void handleRemoveUserFromGroupChat(Message message) throws IOException {
        String sender = message.getFrom();
        String recipient = message.getTo();
        Message removeMessage = new Message();
        removeMessage.setFrom(sender);
        removeMessage.setTo(recipient);
        removeMessage.setType(MessageType.REMOVEUSERFROMGROUPCHAT);
        removeMessage.setStatus("removed from chat");
        if (server.isAdmin(user)) {
            Admin admin = (Admin) user;
            admin.removeUserFromGroupChat(recipient, server.getChatRooms());
        }
        for(ClientListener listener: server.getListenerList()) {
            if (listener.isMemberOfChat(sender)
                    && listener.getUser().getLogin().equals(recipient)) {
                listener.send(removeMessage);
            }
        }
    }

    private void handleBanUser(Message message) throws IOException {
        String recipient = message.getTo();
        Message banMessage = new Message();
        banMessage.setTo(recipient);
        banMessage.setType(MessageType.ADDTOBANLIST);
        banMessage.setStatus("banned");
        if (server.isAdmin(user)) {
            Admin admin = (Admin) user;
            admin.addToBanList(server.getUser(recipient));
        }
        for (ClientListener listener: server.getListenerList()) {
            if (listener.getUser().getLogin().equals(recipient)) {
                listener.send(banMessage);
            }
        }
    }

    private void handleUnBanUser(Message message) throws IOException {
        String recipient = message.getTo();
        Message unBanMessage = new Message();
        unBanMessage.setTo(recipient);
        unBanMessage.setType(MessageType.ADDTOBANLIST);
        unBanMessage.setStatus("unbanned");
        if (server.isAdmin(user)) {
            Admin admin = (Admin) user;
            admin.removeFromBanList(server.getUser(recipient));
        }
        for (ClientListener listener: server.getListenerList()) {
            if (listener.getUser().getLogin().equals(recipient)) {
                listener.send(unBanMessage);
            }
        }
    }

    private boolean isMemberOfChat(String chatName) {
        boolean result = false;
        for(ChatRoom chatRoom: server.getChatRooms()) {
            if (chatRoom.getChatName().equals(chatName)) {
                result = true;
            }
        }
        return result;
    }

    private void handleMessage(Message message) throws IOException {
        String sendTo = message.getTo();
        boolean isGroupMessage = sendTo.startsWith("#");
        List<ClientListener> listenerList = server.getListenerList();
        for(ClientListener listener: listenerList) {
            if (isGroupMessage) {
                if (listener.isMemberOfChat(sendTo)
                        && !getUser().equals(listener.getUser())) {
                    listener.send(message);
                }
            } else {
                if (sendTo.equals(listener.getUser().getLogin())) {
                    listener.send(message);
                }
            }
        }

    }

    private void handleLogOff() throws IOException {
        server.removeListener(this);
        List<ClientListener> listenerList = server.getListenerList();
        //send online users current user's status
        Message offlineMsg = new Message();
        offlineMsg.setStatus("offline");
        offlineMsg.setType(MessageType.OFFLINE);
        if (getUser() != null) {
            offlineMsg.setFrom(getUser().getLogin());
            for(ClientListener listener: listenerList) {
                if (!getUser().getLogin().equals(listener.getUser().getLogin())) {
                    listener.send(offlineMsg);
                }
            }
            clientSocket.close();
            System.out.println(getUser().getLogin() + " has disconnected");
        }
    }

    private void handleRegister(OutputStream outputStream, Message message) throws IOException {
        String login = message.getFrom();
        String password = message.getBody();
        for(User user: server.getUsers()) {
            if (user.getLogin().equals(login)) {
                System.err.println("User " + login + " already exists");
                Message msg = new Message();
                msg.setStatus("Registration failed");
                outputStream.write(messageController.createMessage(msg).getBytes());
                server.removeListener(this);
                return;
            }
        }
        user = new User(login, password, new Date());
        server.getUsers().add(user);

        try {
            IOworker.writeBinary(server.getUsers(), USERS_DATA);
        } catch (IOException e) {
            LOGGER.error("Error reading file with users: ", e);
        }

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
        boolean userExists = false;
        for(User user: server.getUsers()) {
            if (login.equals((user.getLogin()))
                && password.equals(user.getPassword())) {
                userExists = true;
                this.user = user;

            }
        }
        if (userExists) {
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

    private void handleUpdateProfile(OutputStream outputStream, Message message) throws IOException {
        String newLogin = message.getTo();
        String password = message.getBody();
        boolean credentialsExist = false;
        for (User user: server.getUsers()) {
            if ((!user.equals(this.user) && newLogin.equalsIgnoreCase(user.getLogin()))
                    || (!user.equals(this.user) && password.equals(user.getPassword()))) {
                credentialsExist = true;
                break;
            }
        }

        if (credentialsExist) {
            Message msg = new Message();
            msg.setStatus("Profile update failed");
            msg.setType(MessageType.UPDATEPROFILE);
            outputStream.write(messageController.createMessage(msg).getBytes());
            System.err.println("Profile update failed for: " + user.getLogin());
        } else {
            this.user.setLogin(newLogin);
            this.user.setPassword(password);
            Message msg = new Message();
            msg.setStatus("Profile updated");
            msg.setType(MessageType.UPDATEPROFILE);
            outputStream.write(messageController.createMessage(msg).getBytes());
            System.out.println("User updated profile successfully: " + newLogin);
        }
    }

    private void sendCurrUserAllOtherLogins(String login, List<ClientListener> listenerList) throws IOException {
        for(ClientListener listener: listenerList) {
            if (listener.getUser() != null && !login.equals(listener.getUser().getLogin())) {
                Message onlineMsg1 = new Message();
                onlineMsg1.setStatus("online");
                onlineMsg1.setType(MessageType.ONLINE);
                onlineMsg1.setFrom(listener.getUser().getLogin());
                send(onlineMsg1);
            }
        }
    }

    private void sendOnlineUsersCurrUserStatus(String login, List<ClientListener> listenerList) throws IOException {
        Message onlineMsg = new Message();
        onlineMsg.setStatus("online");
        onlineMsg.setType(MessageType.ONLINE);
        onlineMsg.setFrom(login);
        for(ClientListener listener: listenerList) {
            if (listener.getUser() != null && !login.equals(listener.getUser().getLogin())) {
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
