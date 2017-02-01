package com.ercot.cp.ews;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.TimeZone;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@SuppressWarnings("unused")
public class Application {
	
	private static final Logger log = LoggerFactory.getLogger(Application.class);
	
	//SOAP address
	private static final String soap_address = "https://misapitest.ercot.com/2007-08/Nodal/eEDS/EWS/";

	//HttpEndpoints
	private static final String soap_action_market_transactions = "/BusinessService/NodalService.serviceagent/HttpEndPoint/MarketTransactions";
	private static final String soap_action_market_info = "/BusinessService/NodalService.serviceagent/HttpEndPoint/MarketInfo";
	private static final String soap_action_alerts = "/BusinessService/NodalService.serviceagent/HttpEndPoint/Alerts";
	
	//Verbs
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
		SpringApplication.run(Application.class,args);
	}

	@Bean
	CommandLineRunner lookup(EwsClient ewsClient) {
		return args -> {
			try {
				ewsClient.callEWS(soap_address, soap_action_market_info, formRequest());
			} catch (SoapFaultClientException e) {
				log.error("Encountered a soap fault client exception");
				log.error(e.getFaultStringOrReason());
				Source sfceSource = e.getSoapFault().getSource();
				StringResult sr = new StringResult();
				TransformerFactory.newInstance().newTransformer().transform(sfceSource, sr);
				log.error(sr.toString());
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
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS-06:00");
		
		requestHeader.setVerb(_get);
		
		requestHeader.setNoun("SystemStatus");
		
		nonce.setValue("201009071023207");
		rdt.setNonce(nonce);
		created.setValue(format.format(createdDate));
		rdt.setCreated(created);
		requestHeader.setReplayDetection(rdt);
		
		requestHeader.setRevision("1");
		
		//TODO: Set source to CN (common name) on your certificate
		requestHeader.setSource("");
		
		//TODO: Set user id to EmployeeID on your certificate
		requestHeader.setUserID("");
		
		requestHeader.setMessageID("20110719SJ1");
		
		requestHeader.setComment("InternalUIMpDunsNumber$0000000000001");
		
		//Build Request
		RequestType requestRequest = new RequestType();
		requestRequest.getID().add("REQ.20160415.COP");
	
		//Set header and request
		request.setHeader(requestHeader);
		request.setRequest(requestRequest);
		
		return request;
	}

}
