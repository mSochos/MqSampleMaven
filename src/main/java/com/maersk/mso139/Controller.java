package com.maersk.mso139;

import com.ibm.mq.MQException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class Controller {

    @Autowired
    MqConfig configProperties;

    @Autowired
    MqClient mqClient;

    @GetMapping("/sendmessage")
    public String getHome()  {
        try {
            mqClient.init();
            mqClient.sendToQueue();
        } catch (MQException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Thanks for waiting. Messages sent to "+configProperties.hostname + configProperties.channel+configProperties.port+configProperties.qManager+configProperties.inputQName+configProperties.outputQName);
        return "Thanks for waiting. Messages sent.";
    }
}
