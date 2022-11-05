package com.scheible.nextcloudtalknotifier.poller;

import com.scheible.nextcloudtalknotifier.client.gotify.GotifyClient;
import com.scheible.nextcloudtalknotifier.client.gotify.MessageDto;
import com.scheible.nextcloudtalknotifier.client.talk.RoomDto;
import com.scheible.nextcloudtalknotifier.client.talk.TalkClient;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 *
 * @author sj
 */
@Component
public class Poller {

	private static final Logger logger = LoggerFactory.getLogger(Poller.class);

	private final Set<String> users;

	private final TalkClient talkClient;
	private final TalkUsers talkUsers;

	private final GotifyClient gotifyClient;
	private final GotifyAppTokens gotifyAppTokens;

	private final Map<String, Long> latestTimestamps = new ConcurrentHashMap<>();

	public Poller(TalkClient talkClient, TalkUsers talkUsers, GotifyClient gotifyClient, GotifyAppTokens gotifyAppTokens) {
		if (talkUsers.passwords() == null || gotifyAppTokens.appTokens() == null
				|| !talkUsers.passwords().keySet().equals(gotifyAppTokens.appTokens().keySet())) {
			throw new IllegalStateException("Nextcloud Talk and Gotify users must match and at least one must be configured! "
					+ "Instead found '" + nullableMapKeySetToString(talkUsers.passwords()) + "' Talk users and '"
					+ nullableMapKeySetToString(gotifyAppTokens.appTokens()) + "' Gotify users.'");
		}

		this.users = Collections.unmodifiableSet(new HashSet<>(talkUsers.passwords().keySet()));
		logger.info("Will poll for following users: " + this.users.stream().collect(Collectors.joining(", ")));

		this.talkClient = talkClient;
		this.talkUsers = talkUsers;

		this.gotifyClient = gotifyClient;
		this.gotifyAppTokens = gotifyAppTokens;
	}

	private static String nullableMapKeySetToString(Map<String, String> map) {
		return map != null ? map.keySet().stream().collect(Collectors.joining(", ")) : "no";
	}

	@Scheduled(cron = "${notifier.poller.cron:-}")
	public void poll() {
		for (var user : this.users) {
			var previousTimestamp = this.latestTimestamps.getOrDefault(user, 0l);

			var beforeTalkCall = System.nanoTime();
			var conversations = this.talkClient.getConversations(user, talkUsers.passwords().get(user));
			var talkCallDuration = Duration.ofNanos(System.nanoTime() - beforeTalkCall);

			var latestMessageTimestamp = conversations.getLatestMessageTimestamp();

			if (latestMessageTimestamp.isPresent()) {
				this.latestTimestamps.put(user, latestMessageTimestamp.getAsLong());

				if (previousTimestamp == 0l) {
					logger.info("Initial polling of Nextcloud Talk for '" + user + "' (talk call duration: "
							+ talkCallDuration.toMillis() + " ms).");
				} else {
					var roomsToNotify = getRoomsToNotify(conversations.rooms(), previousTimestamp, user);
					if (roomsToNotify.isEmpty()) {
						logger.info("No rooms to notify for '" + user + "' (talk call duration: "
								+ talkCallDuration.toMillis() + " ms).");
					} else {
						var maxDateTime = Instant.ofEpochSecond(latestMessageTimestamp.getAsLong())
								.atZone(ZoneId.systemDefault());
						var message = roomsToNotify.stream().map(RoomDto::displayName)
								.sorted().collect(Collectors.joining(", "));

						var beforeGotifyCall = System.nanoTime();
						this.gotifyClient.sendNotification(new MessageDto("Next Cloud Talk", message, 8), user,
								this.gotifyAppTokens.appTokens().get(user));
						var gotifyCallDuration = Duration.ofNanos(System.nanoTime() - beforeGotifyCall);

						logger.info("Notified " + roomsToNotify.size() + " room(s) for '" + user + "' with last message "
								+ "timestamp of " + maxDateTime + " (talk call duration: " + talkCallDuration.toMillis()
								+ " ms, notify call duration: " + gotifyCallDuration.toMillis() + " ms).");
					}
				}
			}
		}
	}

	static List<RoomDto> getRoomsToNotify(List<RoomDto> rooms, long minTimestamp, String user) {
		return rooms.stream().filter(room -> !room.lastMessage().actorId().equals(user)
				&& room.lastMessage().timestamp() > minTimestamp).toList();
	}
}
