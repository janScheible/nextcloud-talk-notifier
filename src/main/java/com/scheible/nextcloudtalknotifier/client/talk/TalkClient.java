package com.scheible.nextcloudtalknotifier.client.talk;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import okhttp3.Call;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 *
 * @author sj
 */
@Component
public class TalkClient {

	private final ObjectMapper mapper = new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

	private final OkHttpClient httpClient;
	private final String baseUrl;

	public TalkClient(OkHttpClient httpClient, @Value("${notifier.client.talk.base-url}") String baseUrl) {
		this.httpClient = httpClient;
		this.baseUrl = baseUrl;
	}
	
	public ConversationsDto getConversations(String user, String password) {
		return getConversations(this.baseUrl, user, password);
	}

	public ConversationsDto getConversations(String baseUrl, String user, String password) {
		Request request = new Request.Builder().url(baseUrl + "/ocs/v2.php/apps/spreed/api/v4/room")
				.header("Authorization", Credentials.basic(user, password, StandardCharsets.UTF_8))
				.header("Accept", "application/json").header("OCS-APIRequest", "true").build();

		String body = null;
		try {
			Call call = this.httpClient.newCall(request);
			Response response = call.execute();
			body = response.body().string();
		} catch (IOException ex) {
			throw new UncheckedIOException("Error while " + request.newBuilder()
					.header("Authorization", "'of " + user + "'").build(), ex);
		}

		try {
			return this.mapper.readValue(body, ConversationsDto.class);
		} catch (JsonProcessingException ex) {
			throw new UncheckedIOException("Error while deserializing '" + body.replaceAll("\\R", "") + "'", ex);
		}
	}
}
