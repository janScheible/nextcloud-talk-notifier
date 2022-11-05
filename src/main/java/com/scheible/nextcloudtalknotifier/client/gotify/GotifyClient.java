package com.scheible.nextcloudtalknotifier.client.gotify;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.UncheckedIOException;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 *
 * @author sj
 */
@Component
public class GotifyClient {

	private final ObjectMapper mapper = new ObjectMapper();

	private final OkHttpClient httpClient;
	private final String baseUrl;

	public GotifyClient(OkHttpClient httpClient, @Value("${notifier.client.gotify.base-url}") String baseUrl) {
		this.httpClient = httpClient;
		this.baseUrl = baseUrl;
	}
	
	public MessageResultDto sendNotification(MessageDto messageDto, String user, String appToken) {
		return sendNotification(this.baseUrl, messageDto, user, appToken);
	}

	public MessageResultDto sendNotification(String baseUrl, MessageDto messageDto, String user, String appToken) {
		String messageJson;

		try {
			messageJson = this.mapper.writeValueAsString(messageDto);
		} catch (JsonProcessingException ex) {
			throw new UncheckedIOException("Error while serializing " + messageDto, ex);
		}

		Request request = new Request.Builder().url(baseUrl + "/message").header("X-Gotify-Key", appToken)
				.post(RequestBody.create(messageJson, MediaType.parse("application/json"))).build();

		String body = null;
		try {
			Call call = this.httpClient.newCall(request);
			Response response = call.execute();
			body = response.body().string();
		} catch (IOException ex) {
			throw new UncheckedIOException("Error while " + request.newBuilder()
					.header("X-Gotify-Key", "'of " + user + "'").build(), ex);
		}

		try {
			return this.mapper.readValue(body, MessageResultDto.class);
		} catch (IOException ex) {
			throw new UncheckedIOException("Error while deserializing '" + body.replaceAll("\\R", "") + "'", ex);
		}
	}
}
