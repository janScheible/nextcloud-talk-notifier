package com.scheible.nextcloudtalknotifier.poller;

import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 *
 * @author sj
 */
@ConfigurationProperties(prefix = "notifier.client.talk")
public record TalkUsers(Map<String, String> passwords) {
	
}
