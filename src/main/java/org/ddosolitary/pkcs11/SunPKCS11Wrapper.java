package org.ddosolitary.pkcs11;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class SunPKCS11Wrapper extends Provider {
	@SuppressWarnings("unchecked")
	public SunPKCS11Wrapper(String cfg) {
		super(
			"org.ddosolitary.apksigner.SunPKCS11Wrapper",
			"11",
			"A wrapper for SunPKCS11 providing compatibility with Java 8"
		);
		Provider provider = Security.getProvider("SunPKCS11").configure(cfg);
		provider.forEach(this::put);
		provider.getServices().forEach(s -> {
			try {
				Method aliasMethod = Provider.Service.class.getDeclaredMethod("getAliases");
				aliasMethod.setAccessible(true);
				Field attrField = Provider.Service.class.getDeclaredField("attributes");
				attrField.setAccessible(true);
				Map<String, String> attrs = ((Map<Object, String>)attrField.get(s))
					.entrySet().stream().collect(Collectors.toMap(
						e -> e.getKey().toString(),
						Map.Entry::getValue
					));
				putService(new ProviderService(this, s, (List<String>)aliasMethod.invoke(s), attrs));
			} catch (Exception ignored) {}
		});
	}

	private static final class ProviderService extends Provider.Service {
		private final Service service;

		public ProviderService(
			Provider provider,
			Provider.Service service,
			List<String> aliases,
			Map<String, String> attributes
		) {
			super(
				provider,
				service.getType(),
				service.getAlgorithm(),
				service.getClassName(),
				aliases,
				attributes
			);
			this.service = service;
		}

		@Override
		public Object newInstance(Object constructorParameter) throws NoSuchAlgorithmException {
			return service.newInstance(constructorParameter);
		}

		@Override
		public boolean supportsParameter(Object parameter) {
			return service.supportsParameter(parameter);
		}
	}
}
