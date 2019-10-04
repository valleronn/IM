package com.nc.controller;

import com.nc.model.IOworker;
import com.nc.model.users.Admin;
import com.nc.model.users.ChatRoom;
import com.nc.model.users.User;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.*;
import org.apache.log4j.Logger;

public class Server extends Thread {

    private final static Logger LOGGER = Logger.getLogger(Server.class);
    private static final File USERS_DATA = new File("users.bin");

    private int port;
    private List<ClientListener> listenerList = new ArrayList<>();
    private Set<User> users = new HashSet<>();
    private List<ChatRoom> chatRooms = new ArrayList<>();

    public Server(int port) {
        this.port = port;
        try {
            IOworker.readBinary(users, chatRooms, USERS_DATA);
        } catch (IOException e) {
            LOGGER.error("Error reading file with users: ", e);
        }
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                System.out.println("About to accept client connection");
                Socket clientSocket = serverSocket.accept();
                System.out.println("Accepted connection from " + clientSocket);
                ClientListener clientListener = new ClientListener(this, clientSocket);
                listenerList.add(clientListener);
                clientListener.start();
            }
        } catch (SocketException s) {
            LOGGER.error("Server socket error: ", s);
        } catch (IOException e) {
            LOGGER.error("Server start error: ", e);
        }
    }

    public List<ClientListener> getListenerList() {
        return listenerList;
    }

    public void removeListener(ClientListener listener) {
        listenerList.remove(listener);
    }

    public Set<User> getUsers() {
        return users;
    }

    public  User getUser(String login) {
        User currentUser = new User();
        for (User u : users) {
            if (u.getLogin().equals(login)) {
                currentUser = u;
                return currentUser;
            }
        }
        return currentUser;
    }

    public List<ChatRoom> getChatRooms() {
        return chatRooms;
    }

    public boolean chatExists(String chatRoom) {
        boolean chatExists = false;
        for(ChatRoom room: chatRooms) {
            if (room.getChatName().equals(chatRoom)) {
                chatExists = true;
            }
        }
        return chatExists;
    }

    public ChatRoom getChatRoomByName(String chatName) {
        ChatRoom currChatRomm = new ChatRoom();
        for(ChatRoom chatRoom: chatRooms) {
            if (chatRoom.getChatName().equals(chatName)) {
                currChatRomm = chatRoom;
                return currChatRomm;
            }
        }
        return currChatRomm;
    }

    public boolean isAdmin(User user) {
        if (user instanceof Admin) {
            return true;
        } else {
            return false;
        }
    }
}
