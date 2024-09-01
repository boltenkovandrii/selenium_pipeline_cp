package com.andrii.test.base;


import org.apache.commons.configuration.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestConfig {
    private CompositeConfiguration config;
    private static final TestConfig instance = new TestConfig();
    private static final Logger LOGGER = LoggerFactory.getLogger(TestConfig.class.getName());


    private TestConfig(){
        config =  new CompositeConfiguration();
        config.addConfiguration(new SystemConfiguration());
        config.addConfiguration(new EnvironmentConfiguration());
        try {
            config.addConfiguration(new PropertiesConfiguration("config.properties"));
        } catch (ConfigurationException e) {
            LOGGER.error("Failed to read configuration: ", e);
        }
    }

    public static CompositeConfiguration getConfiguration(){
        return instance.config;
    }

}
