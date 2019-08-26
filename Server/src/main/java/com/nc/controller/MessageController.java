package com.nc.controller;

import com.nc.ServerApp;
import com.nc.model.message.Message;
import org.apache.log4j.Logger;


import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;


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
}
