package com.sakha.services.config;

import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.shield.ShieldPlugin;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

/**
 * Created by root on 22/12/15.
 */
@Configuration
public class ElasticSearchConfig {

    @Value("${ELASTIC_SEARCH_SERVER}")
    private String serverIP;
    @Value("${ELASTIC_SEARCH_PORT}")
    private int serverPort;
    @Value("${ELASTIC_SEARCH_CLUSTERNAME}")
    private String clusterName;
    //private IndicesAdminClient adminClient;

    @Bean(name = "esClient")
    public TransportClient esClient() {

        TransportClient client = null;
        try {
            client = TransportClient.builder()
                    .addPlugin(ShieldPlugin.class)
                    .settings(Settings.builder()
                            .put("cluster.name", this.clusterName)
                            .put("client.transport.ping_timeout", "60s")
                            .put("client.transport.sniff", true)).build()
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(this.serverIP), this.serverPort));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        return client;
    }


}
