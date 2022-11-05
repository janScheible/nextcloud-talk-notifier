package com.scheible.nextcloudtalknotifier.client.talk;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
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
class TalkClientIT extends AbstractIntegrationTest {

	@Autowired
	private TalkClient talkClient;

	@Test
	void testGetConversations(WireMockRuntimeInfo wmRuntimeInfo) throws UnsupportedEncodingException, IOException {
		String conversationsJson = new String(new ClassPathResource("next-cloud-talk-conversations.json")
				.getInputStream().readAllBytes(), "UTF8");
		wmRuntimeInfo.getWireMock().register(get("/ocs/v2.php/apps/spreed/api/v4/room")
				.withBasicAuth("user", "password").withHeader("OCS-APIRequest", equalTo("true"))
				.willReturn(okJson(conversationsJson)));

		ConversationsDto conversations = talkClient.getConversations(wmRuntimeInfo.getHttpBaseUrl(), "user", "password");

		assertThat(conversations).isNotNull();
		assertThat(conversations.rooms()).hasSize(2);
	}
}
