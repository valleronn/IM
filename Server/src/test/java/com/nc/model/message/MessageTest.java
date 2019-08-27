package com.nc.model.message;

import org.junit.Assert;
import org.junit.Test;

import javax.xml.bind.*;

import java.io.StringReader;


public class MessageTest {
    private static String TEST_XML="<?xml version = \"1.0\" ?>" +
            "<message>" +
            "<messageID>123456789</messageID>" +
            "<status>test</status>" +
            "<from>test</from>" +
            "<to>test</to>" +
//            "<code1>XXX</code1>" + // uncommit for check exception
            "<body>abc</body>" +
            "</message>";
    @Test
    public void test() throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(Message.class);
        Unmarshaller unmarshaller = jc.createUnmarshaller();

        unmarshaller.setEventHandler(new ValidationEventHandler() {
            @Override
            public boolean handleEvent(ValidationEvent event) {
                return false;
            }
        });


        Message message = (Message)
                unmarshaller.unmarshal(new StringReader(TEST_XML));

        Assert.assertEquals(123456789, message.getMessageID());
        Assert.assertEquals("test", message.getStatus());
        Assert.assertEquals("test", message.getFrom());
        Assert.assertEquals("test", message.getTo());
        Assert.assertEquals("abc", message.getBody());

    }

}