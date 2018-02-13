package com.maersk.mso139;

import java.io.IOException;

import com.ibm.mq.MQC;
import com.ibm.mq.MQEnvironment;
import com.ibm.mq.MQException;
import com.ibm.mq.MQGetMessageOptions;
import com.ibm.mq.MQMessage;
import com.ibm.mq.MQQueue;
import com.ibm.mq.MQQueueManager;
import com.ibm.mq.constants.MQConstants;

public class MqClientApp {
    private MQQueueManager _queueManager = null;
    public int port = 1415;
    public String hostname = "scrbscydk003201.crb.apmoller.net";
    public String channel = "CH.OLYC.TO.STCYTEST1";
    public String qManager = "STCYTEST1";
    public String inputQName = "OLYC.SPT.REQUEST";
    public String outputQName = "OLYC.SPT.REQUEST";
    MQQueue inputQueue;
    MQQueue outputQueue;

    public static void main(String[] args) throws MQException, IOException {
        MqClientApp mqClientApp = new MqClientApp();

        mqClientApp.init();

        mqClientApp.sendToQueue("Hello Simon");
        mqClientApp.sendToQueue("Test message from provider");

        mqClientApp.readFromQueue();
    }

    private void init() throws MQException {

        MQEnvironment.properties.put(MQConstants.HOST_NAME_PROPERTY, hostname);
        MQEnvironment.properties.put(MQConstants.PORT_PROPERTY, port);
        MQEnvironment.properties.put(MQConstants.CHANNEL_PROPERTY, channel);
        MQEnvironment.properties.put(MQConstants.TRANSPORT_PROPERTY, MQConstants.TRANSPORT_MQSERIES_CLIENT);
        MQQueueManager mqQueueManager = new MQQueueManager(qManager);

        inputQueue = mqQueueManager.accessQueue(inputQName, MQConstants.MQOO_INQUIRE + MQConstants.MQOO_FAIL_IF_QUIESCING + MQConstants.MQOO_INPUT_SHARED);
        System.out.println("Connected to input queue successful");

        outputQueue = mqQueueManager.accessQueue(outputQName, MQConstants.MQOO_OUTPUT);
        System.out.println("Connected to output queue successful");
    }

    private void readFromQueue() throws MQException, IOException {
        MQMessage inputMessage = new MQMessage();

        MQGetMessageOptions getOptions = new MQGetMessageOptions();
        getOptions.options = MQConstants.MQGMO_NO_WAIT + MQConstants.MQGMO_FAIL_IF_QUIESCING + MQConstants.MQGMO_CONVERT;

        System.out.println("Current queue depth: "+inputQueue.getCurrentDepth());
        inputQueue.get(inputMessage, getOptions);
        System.out.println("Message consumed");
        byte[] byteText = new byte[inputMessage.getMessageLength()];
        inputMessage.readFully(byteText);
        String data = new String(byteText);
        System.out.println("Message polled from queue: "+data);
        System.out.println("Queue depth after reading message: "+inputQueue.getCurrentDepth());
    }

    private void sendToQueue(String message) throws MQException, IOException {
        MQMessage mqMessage = new MQMessage();
        mqMessage.messageId = new byte[]{123};
        mqMessage.correlationId = new byte[]{123};
        mqMessage.replyToQueueManagerName = "replyToQueueManagerName";
        mqMessage.replyToQueueName = "replyToQueueName";
        mqMessage.format = MQC.MQFMT_STRING;
        mqMessage.expiry = 500000;
        mqMessage.writeString(message);
        outputQueue.put(mqMessage);

        System.out.println("Message sent");
    }
}