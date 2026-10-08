package com.andrii.test.base;


import org.apache.commons.configuration2.CompositeConfiguration;
import org.apache.commons.configuration2.EnvironmentConfiguration;
import org.apache.commons.configuration2.SystemConfiguration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class TestConfig {
    private final CompositeConfiguration config;
    private static final TestConfig INSTANCE = new TestConfig();
    private static final Logger LOGGER = LoggerFactory.getLogger(TestConfig.class.getName());


    private TestConfig() {
        config = new CompositeConfiguration();
        config.addConfiguration(new SystemConfiguration());
        config.addConfiguration(new EnvironmentConfiguration());
        try {
            Configurations configs = new Configurations();
            config.addConfiguration(configs.properties("config.properties"));
        } catch (ConfigurationException e) {
            LOGGER.error("Failed to read configuration: ", e);
        }
    }

    public static CompositeConfiguration getConfiguration() {
        return INSTANCE.config;
    }

}
