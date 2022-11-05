package com.scheible.nextcloudtalknotifier.client;

import java.time.Duration;
import java.util.List;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author sj
 */
@Configuration(proxyBeanMethods = false)
class HttpClientConfiguration {

	@Bean
	OkHttpClient okHttpClient(@Value("${notifier.http.client.connect-timeout:5s}") Duration connectionTimeout,
			@Value("${notifier.http.client.read-write-timeout:5s}") Duration readWriteTimeout) {
		return new OkHttpClient.Builder().readTimeout(readWriteTimeout).writeTimeout(readWriteTimeout)
				.connectTimeout(connectionTimeout).protocols(List.of(Protocol.HTTP_1_1)).build();
	}
}
