package com.nc.controller;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;

public class ConfigReader {

    private final static Logger LOGGER = Logger.getLogger(Server.class);
    private final static File file = new File("connect.xml");

    private int port;
    private String ip;

    public ConfigReader() {
        getConfigConnect();
    }

    private void getConfigConnect() {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = null;
        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            LOGGER.error("Parser Configuration Exception: ", e);
        }
        Document document = null;
        try {
            document = documentBuilder.parse(file);
        } catch (SAXException e) {
            LOGGER.error("Parser exception: ", e);
        } catch (IOException e) {
            LOGGER.error("IO Exception when try to read config file ", e);
        }
        int port = Integer.parseInt(document.getElementsByTagName("serverPort").item(0).getTextContent());
        String ip = document.getElementsByTagName("serverIP").item(0).getTextContent();
        this.ip = ip;
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public String getIp() {
        return ip;
    }
}
