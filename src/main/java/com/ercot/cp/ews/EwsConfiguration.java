package com.ercot.cp.ews;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.soap.security.support.KeyStoreFactoryBean;
import org.springframework.ws.soap.security.xwss.XwsSecurityInterceptor;
import org.springframework.ws.soap.security.xwss.callback.KeyStoreCallbackHandler;

@Configuration
public class EwsConfiguration {

	@Bean
	public Jaxb2Marshaller marshaller() {
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		marshaller.setContextPath("com.ercot.schema._2007_06.nodal.ews.message");
		return marshaller;
	}
	
	@Bean
	public KeyStoreFactoryBean keyStore() {
		KeyStoreFactoryBean keystore = new KeyStoreFactoryBean();
		keystore.setPassword("changeit");
		keystore.setLocation(new ClassPathResource("clientcert.jks"));
		return keystore;
	}
	
	@Bean
	public KeyStoreCallbackHandler keyStoreHandler() {
		KeyStoreCallbackHandler keyStoreHandler = new KeyStoreCallbackHandler();
		keyStoreHandler.setKeyStore(keyStore().getObject());
		keyStoreHandler.setPrivateKeyPassword("test");
		keyStoreHandler.setDefaultAlias("clientcert");
		return keyStoreHandler;
	}
	
	@Bean
	public XwsSecurityInterceptor securityInterceptor() {
		XwsSecurityInterceptor securityInterceptor = new XwsSecurityInterceptor();
		securityInterceptor.setCallbackHandler(keyStoreHandler());
		securityInterceptor.setPolicyConfiguration(new ClassPathResource("SecurityPolicy.xml"));
		return securityInterceptor;
	}

	@Bean
	public EwsClient ewsClient(Jaxb2Marshaller marshaller) {
		EwsClient client = new EwsClient();
		client.setMarshaller(marshaller);
		client.setUnmarshaller(marshaller);
		client.setInterceptors(new ClientInterceptor[]{securityInterceptor()});
		return client;
	}

}
