package com.nc.model.message;

import org.junit.Assert;
import org.junit.Test;

import javax.xml.bind.*;

import java.io.StringReader;


public class MessageTest {
    private static String TEST_XML="<?xml version = \"1.0\" ?>" +
            "<message xmlns=\"http://foobar.com\">" +
            "<messageID>123456789</messageID>" +
            "<status>101</status>" +
            "<from>147</from>" +
            "<to>258</to>" +
//            "<code1>XXX</code1>" + // uncommit for check exception
            "<body>abc</body>" +
            "</message>";
    @Test
    public void test() throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance("com.nc.model.message");
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
        Assert.assertEquals(101, message.getStatus());
        Assert.assertEquals(147, message.getFrom());
        Assert.assertEquals(258, message.getTo());
        Assert.assertEquals("abc", message.getBody());

    }

}