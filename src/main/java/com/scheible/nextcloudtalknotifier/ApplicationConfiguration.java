package com.scheible.nextcloudtalknotifier;

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 *
 * @author sj
 */
@Configuration(proxyBeanMethods = false)
@EnableScheduling
@ConfigurationPropertiesScan
class ApplicationConfiguration {
	
}
