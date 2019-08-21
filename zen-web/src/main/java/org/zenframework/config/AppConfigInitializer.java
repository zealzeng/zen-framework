package org.zenframework.config;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;

/**
 * Application configuration initializer
 * Created by Zeal on 2019/1/12 0012.
 */
public abstract class AppConfigInitializer implements ApplicationContextInitializer {

    /**
     * Find default application configuration properties
     * @return
     */
    protected abstract PropertySource getAppConfigs();


    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {

        ConfigurableEnvironment _environment = applicationContext.getEnvironment();
        if (_environment instanceof AbstractEnvironment) {
            AbstractEnvironment environment = (AbstractEnvironment) _environment;
            PropertySource appConfigs = this.getAppConfigs();
            if (appConfigs != null) {
                environment.getPropertySources().addLast(appConfigs);
            }
            else {
            }
        }
        else {
        }
    }
}
