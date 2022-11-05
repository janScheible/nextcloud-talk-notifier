package com.scheible.nextcloudtalknotifier.jasypt;

import java.text.Normalizer;
import org.jasypt.iv.RandomIvGenerator;
import org.jasypt.salt.RandomSaltGenerator;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;

/**
 *
 * @author sj
 */
class JasyptTypeHints implements RuntimeHintsRegistrar {

	@Override
	public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
		hints.reflection().registerType(RandomSaltGenerator.class, MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS);
		hints.reflection().registerType(Normalizer.class,
				MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS, MemberCategory.INVOKE_PUBLIC_METHODS);
		hints.reflection().registerType(Normalizer.Form.class,
				MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS, MemberCategory.PUBLIC_FIELDS);
		hints.reflection().registerType(RandomIvGenerator.class, MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS);
	}
}
