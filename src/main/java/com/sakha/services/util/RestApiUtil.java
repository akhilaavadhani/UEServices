package com.sakha.services.util;

import com.sakha.services.pojo.AssociationsData;
import org.apache.http.entity.StringEntity;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import java.io.UnsupportedEncodingException;

/**
 * Created by root on 23/12/15.
 */
@Component
public class RestApiUtil {

    static final Logger log = Logger.getLogger(RestApiUtil.class);
    private static final String CONTENT_TYPE="application/json";
    private static final String ASSOCIATION = "association";

    @Value("${AI_API_BASEURL}")
    String apiBaseURL;
    public String getAssociation(AssociationsData associationsData) {
        String url=	apiBaseURL+ ASSOCIATION;
        return invokeApi(url,associationsData);
    }

    private String invokeApi(String url,Object requestObject){

        String result="";
        try {
            StringEntity parameter = new StringEntity(requestObject.toString());
            parameter.setContentType(CONTENT_TYPE);
            result = RESTClientUtil.post(url, parameter);
        } catch (UnsupportedEncodingException e) {
            log.error("Exception:",e);
        }
        return result;
    }
}
