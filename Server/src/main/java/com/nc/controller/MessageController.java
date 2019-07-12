package com.nc.controller;

import com.nc.model.message.Message;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;

public class MessageController {


    public Message extractMessage(String rawMessage) {
        InputStream in = new ByteArrayInputStream(rawMessage.getBytes());
        JAXBContext jaxbContext = null;
        Message message = null;
        try {
            jaxbContext = JAXBContext.newInstance(Message.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            message = (Message) jaxbUnmarshaller.unmarshal(in);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return message;
    }

    public String createMessage(Message message) {
        StringWriter sw = new StringWriter();
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Message.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            // output pretty printed
            //jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.marshal(message, sw);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return sw.toString() + "\n"; // \n is obligatory, otherwise the message won't be sent
    }
}
