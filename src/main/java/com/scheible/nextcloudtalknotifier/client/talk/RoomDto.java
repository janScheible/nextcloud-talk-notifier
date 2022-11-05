package com.scheible.nextcloudtalknotifier.client.talk;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author sj
 */
public record RoomDto(@JsonProperty("displayName") String displayName,
		@JsonProperty("lastMessage") LastMessageDto lastMessage) {

}
