package com.sakha.services.config;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.spring.SpringCamelContext;
import org.apache.camel.spring.javaconfig.CamelConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.jms.ConnectionFactory;

/**
 * Created by welcome on 29/12/15.
 */
@Configuration
public class ActiveMQConfig extends CamelConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(ActiveMQConfig.class);

    @Value("${activemq.broker.url}")
    String brokerUrl;

    @Bean
    ConnectionFactory jmsConnectionFactory() {
        LOG.info("Connected to JMS Queue on {} ", brokerUrl);
        return new ActiveMQConnectionFactory(brokerUrl);
    }

}
