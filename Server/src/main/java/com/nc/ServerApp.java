package com.nc;

import com.nc.model.Server;
import com.nc.model.message.Message;
import com.nc.model.message.MessageType;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Scanner;

/**
 * Hello world!
 *
 */
public class ServerApp {
    public static void main(String[] args) {
        int port = 4444;
        Server server = new Server(port);
        server.start();
    }
}
