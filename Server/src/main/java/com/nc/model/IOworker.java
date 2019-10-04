package com.nc.model;

import com.nc.model.users.Admin;
import com.nc.model.users.BanList;
import com.nc.model.users.ChatRoom;
import com.nc.model.users.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.util.*;

public class IOworker {

    public static void write(Set<User> users, List<ChatRoom> chatRooms, OutputStream out) throws IOException {
        if (users == null) {
            throw new NullPointerException();
        }
        int size = users.size();
        DataOutputStream outStream = new DataOutputStream(out);
        try {
            outStream.writeInt(size);
            for(User user: users) {
                outStream.writeUTF(user.getLogin());
                outStream.writeUTF(user.getFullName());
                outStream.writeUTF(user.getPassword());
                outStream.writeLong(user.getRegDate().getTime());
                outStream.writeBoolean(user.isActive());

                int messageListSize = user.getMessageList().size();
                outStream.writeInt(messageListSize);
                for (String f : user.getMessageList()) {
                    outStream.writeUTF(f);
                }

                if (user instanceof Admin) {
                    Admin admin = (Admin) user;
                    BanList banList = admin.getBanList();
                    writeBanList(outStream, banList);
                }
                writeContacts(outStream, user);
                writeChatRooms(outStream, user);
                writeGroupChatContacts(outStream, chatRooms);
            }

        } finally {
            outStream.flush();
            outStream.close();
        }
    }

    private static void writeBanList(DataOutputStream outStream, BanList banList) throws IOException {
        int banListSize = banList.getBanList().size();
        outStream.writeInt(banListSize);
        for (User user: banList.getBanList()) {
            outStream.writeUTF(user.getLogin());
        }
    }

    private static void writeGroupChatContacts(DataOutputStream outStream, List<ChatRoom> chatRooms) throws IOException {
        for(ChatRoom chatRoom: chatRooms) {
            int groupChatContactsSize = chatRoom.getUsers().size();
            outStream.writeInt(groupChatContactsSize);
            for(User user: chatRoom.getUsers()) {
                outStream.writeUTF(user.getLogin());
            }
        }
    }

    private static void writeChatRooms(DataOutputStream outStream, User user) throws IOException {
        int chatRoomSize = user.getChatRooms().size();
        outStream.writeInt(chatRoomSize);
        for(ChatRoom chatRoom: user.getChatRooms()) {
            outStream.writeUTF(chatRoom.getChatName());
        }
    }

    private static void writeContacts(DataOutputStream outStream, User user) throws IOException {
        int contactsListSize = user.getMyContacts().size();
        outStream.writeInt(contactsListSize);
        for(User contact: user.getMyContacts()) {
            outStream.writeUTF(contact.getLogin());
        }
    }

    public static void writeBinary(Set<User> users, List<ChatRoom> chatRooms, File file) throws IOException {
        FileOutputStream outputFile = new FileOutputStream(file);
        try {
            write(users, chatRooms, outputFile);
        } finally {
            outputFile.close();
        }
    }

    public static void read(Set<User> users, List<ChatRoom> chatRooms, InputStream in) throws IOException {
        DataInputStream inputStream = new DataInputStream(in);
        int size = inputStream.readInt();
        try {
            for (int i = 0; i < size; i++) {
                String login = inputStream.readUTF();
                String fullName = inputStream.readUTF();
                String password = inputStream.readUTF();
                Date regDate = new Date(inputStream.readLong());
                Boolean active = inputStream.readBoolean();

                int messageSize = inputStream.readInt();
                List<String> messageList = new LinkedList<>();
                for (int j = 0; j < messageSize; j++) {
                    messageList.add(inputStream.readUTF());
                }

                if ("admin".equals(login)) {
                    int banListSize = inputStream.readInt();
                    BanList banList = new BanList();
                    for (int l = 0; l < banListSize; l++) {
                        User user = new User();
                        user.setLogin(inputStream.readUTF());
                        banList.addBan(user);
                    }
                    Admin admin = new Admin(login, password, regDate, banList);
                    admin.setMessageList(messageList);
                    admin.setMyContacts(readContactsList(inputStream));
                    admin.setChatRooms(readChatRooms(inputStream));
                    chatRooms.addAll(admin.getChatRooms());
                    for(ChatRoom chatRoom: chatRooms) {
                        chatRoom.setUsers(readContactsList(inputStream));
                    }
                    users.add(admin);
                } else {
                    User user = new User();
                    user.setLogin(login);
                    user.setFullName(fullName);
                    user.setPassword(password);
                    user.setRegDate(regDate);
                    user.setActive(active);
                    user.setMessageList(messageList);
                    user.setMyContacts(readContactsList(inputStream));
                    user.setChatRooms(readChatRooms(inputStream));
                    for(ChatRoom chatRoom: user.getChatRooms()) {
                        chatRoom.setUsers(readContactsList(inputStream));
                    }
                    users.add(user);
                }
            }
        } finally {
            inputStream.close();
        }

    }

    private static ObservableList<User> readContactsList(DataInputStream inputStream) throws IOException {
        int contactsSize = inputStream.readInt();
        ObservableList<User> contactsList = FXCollections.observableArrayList();
        for(int k = 0; k < contactsSize; k++) {
            String contactsLogin = inputStream.readUTF();
            User contact = new User();
            contact.setLogin(contactsLogin);
            contactsList.add(contact);
        }
        return contactsList;
    }

    private static List<ChatRoom> readChatRooms(DataInputStream inputStream) throws IOException {
        int chatRoomsSize = inputStream.readInt();
        List<ChatRoom> chatRooms = new ArrayList<>();
        for(int i = 0; i < chatRoomsSize; i++) {
            String chatName = inputStream.readUTF();
            ChatRoom chatRoom = new ChatRoom();
            chatRoom.setChatName(chatName);
            chatRooms.add(chatRoom);
        }
        return chatRooms;
    }

    public static void readBinary(Set<User> users, List<ChatRoom> chatRooms, File file) throws IOException {
        FileInputStream inputFile = new FileInputStream(file);
        try {
            read(users, chatRooms, inputFile);
        } finally {
            inputFile.close();
        }
    }
}
