package com.scheible.nextcloudtalknotifier.client;

import com.scheible.nextcloudtalknotifier.client.gotify.MessageDto;
import com.scheible.nextcloudtalknotifier.client.gotify.MessageResultDto;
import com.scheible.nextcloudtalknotifier.client.talk.ConversationsDto;
import com.scheible.nextcloudtalknotifier.client.talk.LastMessageDto;
import com.scheible.nextcloudtalknotifier.client.talk.RoomDto;
import org.springframework.aot.hint.ExecutableMode;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;

/**
 *
 * @author sj
 */
class JacksonTypeHints implements RuntimeHintsRegistrar {

	@Override
	public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
		register(hints, MessageDto.class);
		register(hints, MessageResultDto.class);

		register(hints, ConversationsDto.class);
		register(hints, LastMessageDto.class);
		register(hints, RoomDto.class);
	}

	private static void register(RuntimeHints hints, Class clazz) {
		// serialize
		for (var method : clazz.getMethods()) {
			hints.reflection().registerMethod(method, ExecutableMode.INVOKE);
		}

		// deserialize
		for (var constructor : clazz.getConstructors()) {
			hints.reflection().registerConstructor(constructor, ExecutableMode.INVOKE);
		}
	}
}
