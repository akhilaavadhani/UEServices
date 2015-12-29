package com.sakha.services.util;

import com.mongodb.BasicDBObject;
import com.sakha.services.config.ElasticSearchConfig;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by root on 23/12/15.
 */
@Component
public class ElasticSearchUtil {
    @Autowired
    private TransportClient esClient;

    public void insertDocument(String index, String type,
                               BasicDBObject document, String id) {

        IndexRequest indexRequest = new IndexRequest(index, type, id);
        indexRequest.source(document);
        esClient.index(indexRequest)
                .actionGet();
        esClient.index(indexRequest).actionGet();
    }

    /* Insert or Update document in ES*/
    public void updateRequest(String index, String type,
                              Map<String,Object> updateObject, String id)
    {

        IndexRequest indexRequest = new IndexRequest(index, type, id);
        indexRequest.source(updateObject);

        UpdateResponse response = esClient.prepareUpdate(index, type, id)
                .setDoc(indexRequest)
                .setUpsert(indexRequest)
                .setRefresh(true)
                .execute()
                .actionGet();

    }

    public boolean close(){

        esClient.close();
        return true;
    }

    public Client getClient() {
        return esClient;
    }
}
