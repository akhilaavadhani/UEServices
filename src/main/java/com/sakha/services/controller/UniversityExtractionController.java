package com.sakha.services.controller;

/**
 * Created by root on 21/12/15.
 */


import com.sakha.services.datasourceanalyzer.ProfileAnalysis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
public class UniversityExtractionController {

    @Autowired
    private ProfileAnalysis profileAnalysis;

    @RequestMapping("/")
    public String index() {
        return "Greetings from Spring Boot!";
    }

    @RequestMapping(value = "/startextraction/", method = RequestMethod.GET)
    public String startExtractor(){
        profileAnalysis.extractUniversity();
        return "Successfully Completed Extraction";
    }

    @RequestMapping(value = "/testquery/", method = RequestMethod.GET)
    public String testQuery(){
        profileAnalysis.checkQuery();
        return "Successfully Completed Extraction";
    }

}