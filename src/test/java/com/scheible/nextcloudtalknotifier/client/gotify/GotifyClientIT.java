package com.scheible.nextcloudtalknotifier.client.gotify;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.scheible.nextcloudtalknotifier.AbstractIntegrationTest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;

/**
 *
 * @author sj
 */
@WireMockTest
public class GotifyClientIT extends AbstractIntegrationTest {

	@Autowired
	private GotifyClient gotifyClient;

	@Test
	void testSendNotification(WireMockRuntimeInfo wmRuntimeInfo) throws UnsupportedEncodingException, IOException {
		String messageResultJson = new String(new ClassPathResource("gotify-message-result.json")
				.getInputStream().readAllBytes(), "UTF8");
		wmRuntimeInfo.getWireMock().register(post("/message").withHeader("X-Gotify-Key", equalTo("app-token"))
				.willReturn(okJson(messageResultJson)));

		MessageResultDto result = gotifyClient.sendNotification(wmRuntimeInfo.getHttpBaseUrl(),
				new MessageDto("title", "message", 42), "user", "app-token");

		assertThat(result).isNotNull();
	}
}
