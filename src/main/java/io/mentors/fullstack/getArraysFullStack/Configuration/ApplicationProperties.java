package io.mentors.fullstack.getArraysFullStack.Configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationProperties {
    @Value("${ams.endpointBaseUrl}")
    private String endpointBaseUrl;

    @Value("${ams.endpointUrl}")
    private String endpointUrl;


    //TODO GETTERS AND SETTERS

}
