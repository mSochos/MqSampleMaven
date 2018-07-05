package com.maersk.mso139;

import com.ibm.mq.MQEnvironment;
import com.ibm.mq.MQException;
import com.ibm.mq.MQQueueManager;
import com.ibm.mq.constants.MQConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class MqConfig {
    @Value("${queue.hostname}")
    public String hostname;
    @Value("${queue.port}")
    public int port;
    @Value("${queue.queuemanager}")
    public String qManager;
    @Value("${queue.channel}")
    public String channel;
    @Value("${queue.inputqueue}")
    public String inputQName;
    @Value("${queue.outputqueue}")
    public String outputQName;
}
