package com.scheible.nextcloudtalknotifier.poller;

import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 *
 * @author sj
 */
@ConfigurationProperties(prefix = "notifier.client.gotify")
public record GotifyAppTokens(Map<String, String> appTokens) {
	
}
