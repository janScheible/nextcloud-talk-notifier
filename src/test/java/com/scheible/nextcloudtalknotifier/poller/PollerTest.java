package com.scheible.nextcloudtalknotifier.poller;

import com.scheible.nextcloudtalknotifier.client.talk.LastMessageDto;
import com.scheible.nextcloudtalknotifier.client.talk.RoomDto;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

/**
 *
 * @author sj
 */
class PollerTest {

	@Test
	void testRoomsToNotifyWithNoNewMessages() {
		assertThat(Poller.getRoomsToNotify(List.of(new RoomDto("Room 1", new LastMessageDto(0, "user1"))),
				100, "darkwing")).isEmpty();
	}

	@Test
	void testRoomsToNotifyWithNewMessageFromOtherUser() {
		assertThat(Poller.getRoomsToNotify(List.of(new RoomDto("Room 1", new LastMessageDto(200, "user1"))),
				100, "darkwing")).hasSize(1);
	}

	@Test
	void testRoomsToNotifyWithNewMessageFromPollingUser() {
		assertThat(Poller.getRoomsToNotify(List.of(new RoomDto("Room 1", new LastMessageDto(200, "darkwing"))),
				100, "darkwing")).isEmpty();
	}
}
