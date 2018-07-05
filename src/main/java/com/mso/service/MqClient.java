package com.mso.service;

import com.ibm.mq.*;
import com.ibm.mq.constants.MQConstants;
import com.mso.configuration.MqConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
@Slf4j
public class MqClient {

    @Autowired
    private MqConfig mqConfig;

    public int port;
    public String hostname;
    public String channel;
    public String qManager;
    public String inputQName;
    public String outputQName;

    MQQueue inputQueue;
    MQQueue outputQueue;

    public void init() throws MQException, IOException {
        port = mqConfig.port;
        hostname = mqConfig.hostname;
        channel = mqConfig.channel;
        qManager = mqConfig.qManager;
        inputQName = mqConfig.inputQName;
        outputQName = mqConfig.outputQName;

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

    public void readFromQueue() throws MQException, IOException {
        while (inputQueue.getCurrentDepth() > 20000) {
            MQMessage inputMessage = new MQMessage();

            MQGetMessageOptions getOptions = new MQGetMessageOptions();
            getOptions.options = MQConstants.MQGMO_NO_WAIT + MQConstants.MQGMO_FAIL_IF_QUIESCING + MQConstants.MQGMO_CONVERT;
            System.out.println("Current queue depth: " + inputQueue.getCurrentDepth());
            inputQueue.get(inputMessage, getOptions);
            System.out.println("Message consumed");
            byte[] byteText = new byte[inputMessage.getMessageLength()];
            inputMessage.readFully(byteText);
            String data = new String(byteText);
            log.info("Message: " + data);
            System.out.println("Message polled from queue: " + data);
            System.out.println("Queue depth after reading message: " + inputQueue.getCurrentDepth());
        }
    }

    public void sendToQueue() throws MQException, IOException {
        String file = "155_confirm_closed.gds";

        FileUtils.readLines(new File(file), "UTF-8").stream().forEach(line -> {
            MQMessage mqMessage = new MQMessage();
            mqMessage.messageId = new byte[]{123};
            mqMessage.correlationId = new byte[]{123};
            mqMessage.replyToQueueManagerName = "replyToQueueManagerName";
            mqMessage.replyToQueueName = "replyToQueueName";
            mqMessage.format = MQC.MQFMT_STRING;
            mqMessage.expiry = 500000;
            try {
                mqMessage.writeString(line);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                outputQueue.put(mqMessage);
            } catch (MQException e) {
                e.printStackTrace();
            }
            System.out.println("Message sent");
        });
    }
}
