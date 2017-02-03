package com.ercot.cp.ews;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.soap.security.support.KeyStoreFactoryBean;
import org.springframework.ws.soap.security.xwss.XwsSecurityInterceptor;
import org.springframework.ws.soap.security.xwss.callback.KeyStoreCallbackHandler;

import java.security.KeyStore;

@Configuration
public class EwsConfiguration extends WsConfigurerAdapter{

	@Value("${ews.client.keystore.path}")
	private String keystorePath;

	@Value("${ews.client.keystore.password}")
	private String keystorePwd;

	@Value("${ews.client.keystore.alias}")
	private String alias;

	@Bean
	public Jaxb2Marshaller marshaller() {
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		marshaller.setPackagesToScan("com.ercot.schema._2007_06.nodal.ews.message");
		return marshaller;
	}
	
	@Bean
	public KeyStoreFactoryBean keyStore() {
		KeyStoreFactoryBean keystore = new KeyStoreFactoryBean();
		keystore.setPassword(keystorePwd);
		keystore.setLocation(new ClassPathResource(keystorePath));
		return keystore;
	}
	
	@Bean
	public KeyStoreCallbackHandler keyStoreHandler(KeyStore keyStore) {
		KeyStoreCallbackHandler keyStoreHandler = new KeyStoreCallbackHandler();
		keyStoreHandler.setKeyStore(keyStore);
		keyStoreHandler.setPrivateKeyPassword(keystorePwd);
		keyStoreHandler.setDefaultAlias(alias);
		return keyStoreHandler;
	}
	
	@Bean
	public XwsSecurityInterceptor securityInterceptor(KeyStore keyStore) {
		XwsSecurityInterceptor securityInterceptor = new XwsSecurityInterceptor();
		securityInterceptor.setCallbackHandler(keyStoreHandler(keyStore));
		securityInterceptor.setPolicyConfiguration(new ClassPathResource("SecurityPolicy.xml"));
		return securityInterceptor;
	}

	@Bean
	public EwsClient ewsClient(Jaxb2Marshaller marshaller, ClientInterceptor securityInterceptor) {
		EwsClient client = new EwsClient();
		client.setMarshaller(marshaller);
		client.setUnmarshaller(marshaller);
		client.setInterceptors(new ClientInterceptor[]{securityInterceptor});
		return client;
	}

}
