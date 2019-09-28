package com.nc.model;

import com.nc.model.users.Admin;
import com.nc.model.users.BanList;
import com.nc.model.users.User;

import java.io.*;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class IOworker {

    public static void write(Set<User> users, OutputStream out) throws IOException {
        if (users == null) {
            throw new NullPointerException();
        }
        int size = users.size();
        DataOutputStream outStream = new DataOutputStream(out);
        try {
            outStream.writeInt(size);
            for (User t : users) {
                outStream.writeUTF(t.getLogin());
                outStream.writeUTF(t.getFullName());
                outStream.writeUTF(t.getPassword());
                outStream.writeLong(t.getRegDate().getTime());
                outStream.writeBoolean(t.isActive());

                int messageListSize = t.getMessageList().size();
                outStream.writeInt(messageListSize);
                for (String f : t.getMessageList()) {
                    outStream.writeUTF(f);
                }

                if (t instanceof Admin) {
                    Admin admin = (Admin) t;
                    BanList banList = admin.getBanList();
                    int banListSize = banList.getBanList().size();
                    outStream.writeInt(banListSize);
                    for (User user: banList.getBanList()) {
                        outStream.writeUTF(user.getLogin());
                    }
                }
            }

        } finally {
            outStream.flush();
            outStream.close();
        }
    }

    public static void writeBinary(Set<User> users, File file) throws IOException {
        FileOutputStream outputFile = new FileOutputStream(file);
        try {
            write(users, outputFile);
        } finally {
            outputFile.close();
        }
    }

    public static void read(Set<User> users, InputStream in) throws IOException {
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
                    users.add(admin);
                } else {
                    User user = new User();
                    user.setLogin(login);
                    user.setFullName(fullName);
                    user.setPassword(password);
                    user.setRegDate(regDate);
                    user.setActive(active);
                    user.setMessageList(messageList);
                    users.add(user);
                }
            }
        } finally {
            inputStream.close();
        }

    }

    public static void readBinary(Set<User> users, File file) throws IOException {
        FileInputStream inputFile = new FileInputStream(file);
        try {
            read(users, inputFile);
        } finally {
            inputFile.close();
        }
    }
}
