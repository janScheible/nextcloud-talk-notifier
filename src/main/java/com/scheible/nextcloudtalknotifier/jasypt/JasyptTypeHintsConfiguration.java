package com.scheible.nextcloudtalknotifier.jasypt;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;

/**
 *
 * @author sj
 */
@Configuration(proxyBeanMethods = false)
@ImportRuntimeHints(JasyptTypeHints.class)
class JasyptTypeHintsConfiguration {
	
}
