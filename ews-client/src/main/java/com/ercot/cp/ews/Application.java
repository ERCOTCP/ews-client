package com.ercot.cp.ews;

import java.util.Date;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.ercot.schema._2007_06.nodal.ews.message.*;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.*;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_utility_1_0.*;

@SpringBootApplication
public class Application {
	
	private static final String soap_address_dev = "https://dvleip001.ercot.com:8443/sst/runtime.asvc/com.ercot.eip.wsrb";
	private static final String soap_address_itest = "https://testingapi.ercot.com/2007-08/Nodal/eEDS/EWS/";

	private static final String soap_action_market_transactions = "/BusinessService/NodalService.serviceagent/HttpEndPoint/MarketTransactions";
	private static final String soap_action_market_info = "/BusinessService/NodalService.serviceagent/HttpEndPoint/MarketInfo";
	private static final String soap_action_alerts = "/BusinessService/NodalService.serviceagent/HttpEndPoint/Alerts";
	
	private static final String _cancel = "cancel";
	private static final String _canceled = "canceled";
	private static final String _change = "change";
	private static final String _changed = "changed";
	private static final String _create = "create";
	private static final String _created = "created";
	private static final String _close = "close";
	private static final String _closed = "closed";
	private static final String _delete = "delete";
	private static final String _deleted = "deleted";
	private static final String _get = "get";
	private static final String _reply = "reply";
	private static final String _submit = "submit";
	private static final String _update = "update";
	private static final String _updated = "updated";
	
	public static void main(String[] args) {
		SpringApplication.run(Application.class);
	}

	@Bean
	CommandLineRunner lookup(EwsClient ewsClient) {
		return args -> {
			ewsClient.callEWS(soap_address_itest, soap_action_market_info, formRequest());
		};
	}
	
	private RequestMessage formRequest() {
		RequestMessage request = new RequestMessage();
		HeaderType requestHeader = new HeaderType();
		ReplayDetectionType rdt = new ReplayDetectionType();
		EncodedString nonce = new EncodedString();
		AttributedDateTime created = new AttributedDateTime(); 
		Date createdDate = new Date();
		
		requestHeader.setVerb(_get);
		
		requestHeader.setNoun("SystemStatus");
		
		nonce.setValue("1234");
		rdt.setNonce(nonce);
		created.setValue(createdDate.toString());
		rdt.setCreated(created);
		requestHeader.setReplayDetection(rdt);
		
		requestHeader.setRevision("001");
		
		requestHeader.setSource("QBRAZO");
		
		requestHeader.setUserID("API_NODALTEST");
		
		requestHeader.setMessageID("1234567890");
		
		request.setHeader(requestHeader);
		
		return request;
	}

}
