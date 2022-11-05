package com.scheible.nextcloudtalknotifier.client.talk;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.Collections;
import java.util.List;
import java.util.OptionalLong;

/**
 *
 * @author sj
 */
@JsonTypeName(value = "ocs")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
public record ConversationsDto(@JsonProperty("data") List<RoomDto> rooms) {

	public ConversationsDto {
		rooms = Collections.unmodifiableList(rooms != null ? rooms : Collections.emptyList());
	}
	
	public OptionalLong getLatestMessageTimestamp() {
		return rooms().stream().mapToLong(room -> room.lastMessage().timestamp()).max();
	}
}
