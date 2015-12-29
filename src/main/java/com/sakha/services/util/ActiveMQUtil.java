package com.sakha.services.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultProducerTemplate;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import javax.jms.ObjectMessage;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by welcome on 27/12/15.
 */

@Component
public class ActiveMQUtil {

    private static final Logger log = Logger.getLogger(ActiveMQUtil.class);

    private CamelContext context;
    private ProducerTemplate producerTemplate;

    public void addToQueue(String queue, String content, String _id){

        context = new DefaultCamelContext();
        producerTemplate = context.createProducerTemplate();

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> payload = new HashMap<>();
        Map<String, Object> headers = new HashMap<>();
        try {
            payload = mapper.readValue(content, HashMap.class);
            payload.put("increment_date", new Date().getTime());
            payload.put("_id", _id);

            producerTemplate.sendBodyAndHeaders(queue, payload, headers);

        } catch (Exception e) {
            log.info("ActiveMQUtil Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
