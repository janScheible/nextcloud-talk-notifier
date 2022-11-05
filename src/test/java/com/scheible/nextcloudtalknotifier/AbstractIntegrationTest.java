package com.scheible.nextcloudtalknotifier;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

/**
 *
 * @author sj
 */
@SpringBootTest
@TestPropertySource(properties = {"notifier.client.gotify.base-url=wiremock-baseurl-will-be-used",
	"notifier.client.talk.base-url=wiremock-baseurl-will-be-used", "notifier.client.talk.passwords.user=password",
	"notifier.client.gotify.app-tokens.user=app-token"})
public abstract class AbstractIntegrationTest {

}
