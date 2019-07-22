package com.nc.model;

import com.nc.controller.ClientListener;
import com.nc.model.users.Admin;
import com.nc.model.users.ChatRoom;
import com.nc.model.users.User;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server extends Thread {
    private int port;
    private User user;
    private List<ClientListener> listenerList = new ArrayList<>();
    private Set<User> users = new HashSet<>();
    private Set<ChatRoom> chatRooms = new HashSet<>();

    public Server(int port) {
        this.port = port;
        user = new Admin("admin", "admin", new Date());
        users.add(user);
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while (true) {
                System.out.println("About to accept client connection");
                Socket clientSocket = serverSocket.accept();
                System.out.println("Accepted connection from " + clientSocket);
                ClientListener clientListener = new ClientListener(this, clientSocket);
                listenerList.add(clientListener);
                clientListener.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
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

    public User getUser() {
        return user;
    }

    public Set<ChatRoom> getChatRooms() {
        return chatRooms;
    }
}
