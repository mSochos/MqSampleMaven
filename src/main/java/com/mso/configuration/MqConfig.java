package com.mso.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

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
