package com.nc.controller;

import com.nc.model.message.Message;
import com.nc.model.users.ChatRoom;
import com.nc.model.users.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.log4j.Logger;


import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.util.List;


public class MessageController {

    private final static Logger LOGGER = Logger.getLogger(MessageController.class);

    public Message extractMessage(String rawMessage) {
        InputStream in = new ByteArrayInputStream(rawMessage.getBytes());
        JAXBContext jaxbContext = null;
        Message message = null;
        try {
            jaxbContext = JAXBContext.newInstance(Message.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            message = (Message) jaxbUnmarshaller.unmarshal(in);
        } catch (JAXBException e) {
            LOGGER.error("Unmarshaling exception: ", e);
        }
        return message;
    }

    public String createMessage(Message message) {
        StringWriter sw = new StringWriter();
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Message.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);
            jaxbMarshaller.marshal(message, sw);
        } catch (JAXBException e) {
            LOGGER.error("Marshaling exception: ", e);
        }
        return sw.toString() + "\n"; // \n is obligatory, otherwise the message won't be sent
    }

    /**
     * Gets contact users from a collection and puts them
     * into a string separated by ";" delimiter
     * @param groupChatContacts ObservableList of group chat contacts
     * @return
     */
    public StringBuilder convertContactsToString(List<? extends User> groupChatContacts) {
        StringBuilder usersToString = new StringBuilder();
        for (User user: groupChatContacts) {
            if (usersToString.length() == 0) {
                usersToString.append(user.getLogin());
            } else {
                usersToString.append(";" + user.getLogin());
            }
        }
        return usersToString;
    }

    /**
     * Extracts contact users from a string separated
     * by ";" delimiter and adds them into a collection
     * @param listOfGroupChatUsers string of group chat users
     * @return returns ObservableList of group chat users
     */
    public ObservableList<User> extractContactsFromString(String listOfGroupChatUsers) {
        String[] groupChatContactsArr = listOfGroupChatUsers.split("\\;");
        ObservableList<User> groupChatContacts = FXCollections.observableArrayList();
        for(String login: groupChatContactsArr) {
            User user = new User();
            user.setLogin(login);
            groupChatContacts.add(user);
        }
        return groupChatContacts;
    }

    /**
     * Extracts contact users from a string separated
     * by ";" delimiter and adds them into a collection
     * @param listOfChats string of group chat users
     * @return returns ObservableList of group chat users
     */
    public ObservableList<ChatRoom> extractGroupChatsFromString(String listOfChats) {
        String[] chatsArr = listOfChats.split("\\;");
        ObservableList<ChatRoom> groupChatContacts = FXCollections.observableArrayList();
        for(String login: chatsArr) {
            ChatRoom chatRoom = new ChatRoom();
            chatRoom.setLogin(login);
            groupChatContacts.add(chatRoom);
        }
        return groupChatContacts;
    }
}
