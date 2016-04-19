package com.ercot.cp.ews;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.TimeZone;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerFactory;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.ws.soap.SoapFault;
import org.springframework.ws.soap.SoapFaultDetail;
import org.springframework.ws.soap.SoapFaultDetailElement;
import org.springframework.ws.soap.client.SoapFaultClientException;
import org.springframework.xml.transform.StringResult;

import com.ercot.schema._2007_06.nodal.ews.message.*;

import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.*;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_utility_1_0.*;

@SpringBootApplication
public class Application {
	
	private static final String soap_address_dev = "https://dvleip001.ercot.com:8443/sst/runtime.asvc/com.ercot.eip.wsrb";
	private static final String soap_address_itest = "https://testingapi.ercot.com/2007-08/Nodal/eEDS/EWS/";
	private static final String soap_address_itest_alt = "https://misapitest.ercot.com/2007-08/Nodal/eEDS/EWS/";

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
			try {
				ewsClient.callEWS(soap_address_itest_alt, soap_action_market_info, formRequest());
			} catch (SoapFaultClientException e) {
				System.out.println("Encountered a soap fault client exception");
				System.out.println(e.getFaultStringOrReason());
				Source sfceSource = e.getSoapFault().getSource();
				StringResult sr = new StringResult();
				TransformerFactory.newInstance().newTransformer().transform(sfceSource, sr);
				System.out.println(sr.toString());
			}
		};
	}
	
	private RequestMessage formRequest() {
		RequestMessage request = new RequestMessage();
		
		//Build Header
		HeaderType requestHeader = new HeaderType();
		ReplayDetectionType rdt = new ReplayDetectionType();
		EncodedString nonce = new EncodedString();
		AttributedDateTime created = new AttributedDateTime(); 
		Date createdDate = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS-05:00");
		
		requestHeader.setVerb(_get);
		
		requestHeader.setNoun("SystemStatus");
		
		nonce.setValue("201009071023207");
		rdt.setNonce(nonce);
		created.setValue(format.format(createdDate));
		rdt.setCreated(created);
		requestHeader.setReplayDetection(rdt);
		
		requestHeader.setRevision("1");
		
		requestHeader.setSource("QLUMN1");
		
		requestHeader.setUserID("API_DCUPGRADE");
		
		requestHeader.setMessageID("20110719SJ1");
		
		requestHeader.setComment("InternalUIMpDunsNumber$0000008880001");
		
		//Build Request
		RequestType requestRequest = new RequestType();
		requestRequest.getID().add("QLUMN1.20160415.COP");
		
		
		//Set header and request
		request.setHeader(requestHeader);
		request.setRequest(requestRequest);
		
		return request;
	}

}
