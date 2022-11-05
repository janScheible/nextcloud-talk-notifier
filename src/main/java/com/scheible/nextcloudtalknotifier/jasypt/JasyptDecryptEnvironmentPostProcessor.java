package com.scheible.nextcloudtalknotifier.jasypt;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.logging.Log;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.logging.DeferredLogFactory;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.MapPropertySource;

/**
 * This is a light-weight drop-in replacement for https://github.com/ulisesbocchio/jasypt-spring-boot that works with
 * Spring Native.
 */
public class JasyptDecryptEnvironmentPostProcessor implements EnvironmentPostProcessor {

	private final Log logger;

	public JasyptDecryptEnvironmentPostProcessor(DeferredLogFactory deferredLogFactory) {
		this.logger = deferredLogFactory.getLog(getClass());
	}

	@Override
	public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
		var propertyNames = environment.getPropertySources().stream().flatMap(ps -> ps instanceof EnumerablePropertySource
				? Arrays.asList(((EnumerablePropertySource) ps).getPropertyNames()).stream() : Stream.empty())
				.collect(Collectors.toSet());

		var encryptedProperties = propertyNames.stream().flatMap(name -> {
			var value = environment.getProperty(name);
			return isEncrypted(value) ? Stream.of(Map.entry(name, value)) : Stream.empty();
		}).toList();

		if (encryptedProperties.isEmpty()) {
			return;
		}

		var password = environment.getProperty("jasypt.encryptor.password");
		if (password == null || password.isBlank()) {
			logger.error("'jasypt.encryptor.password' is not set! Properties " + encryptedProperties.stream()
					.map(Entry::getKey).collect(Collectors.joining("', '", "'", "'")) + " can't be decyrpted.");
			return;
		}

		StringEncryptor encryptor = getEncryptor(password);
		Map<String, Object> decryptedProperties = new HashMap<>();

		for (var encryptedProperty : encryptedProperties) {
			var strippedValue = encryptedProperty.getValue().substring(4, encryptedProperty.getValue().length() - 1);

			try {
				var decryptedValue = encryptor.decrypt(strippedValue);
				decryptedProperties.put(encryptedProperty.getKey(), decryptedValue);
			} catch (Exception ex) {
				logger.error("Error while decrypting '" + encryptedProperty.getKey() + "'");
			}
		}

		environment.getPropertySources().addFirst(new MapPropertySource("decrypted-properties", decryptedProperties));
	}

	private static boolean isEncrypted(String value) {
		return value != null && value.startsWith("ENC(") && value.endsWith(")");
	}

	/**
	 * Default config from https://github.com/ulisesbocchio/jasypt-spring-boot#use-you-own-custom-encryptor. Must match
	 * in order to allow the Maven plugin can do it's magic.
	 */
	private static StringEncryptor getEncryptor(String password) {
		PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
		SimpleStringPBEConfig config = new SimpleStringPBEConfig();
		config.setPassword(password);
		config.setAlgorithm("PBEWITHHMACSHA512ANDAES_256");
		config.setKeyObtentionIterations("1000");
		config.setPoolSize("1");
		config.setProviderName("SunJCE");
		config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
		config.setIvGeneratorClassName("org.jasypt.iv.RandomIvGenerator");
		config.setStringOutputType("base64");
		encryptor.setConfig(config);
		return encryptor;
	}
}
