package com.scheible.nextcloudtalknotifier.client.talk;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author sj
 */
public record LastMessageDto(@JsonProperty("timestamp") long timestamp, @JsonProperty("actorId") String actorId) {

}
