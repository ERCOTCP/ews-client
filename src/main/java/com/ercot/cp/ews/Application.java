package com.ercot.cp.ews;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.ws.soap.client.SoapFaultClientException;
import org.springframework.xml.transform.StringResult;

import com.ercot.schema._2007_06.nodal.ews.message.*;

import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.*;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_utility_1_0.*;

@SpringBootApplication
@SuppressWarnings("unused")
public class Application implements CommandLineRunner {

	private static final Logger log = LoggerFactory.getLogger(Application.class);

	//SOAP address
	private static final String SOAP_ADDRESS = "https://misapitest.ercot.com/2007-08/Nodal/eEDS/EWS/";

	//HttpEndpoints
	private static final String SOAP_ACTION_MARKET_TRANSACTIONS = "/BusinessService/NodalService.serviceagent/HttpEndPoint/MarketTransactions";
	private static final String SOAP_ACTION_MARKET_INFO = "/BusinessService/NodalService.serviceagent/HttpEndPoint/MarketInfo";
	private static final String SOAP_ACTION_ALERTS = "/BusinessService/NodalService.serviceagent/HttpEndPoint/Alerts";

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

	//Web service client
	@Autowired
	private EwsClient ewsClient;

	//Set by ews.header.source in src/main/resources/application.properties
	@Value("${ews.header.source}")
	private String sourceName;

	//Set by ews.header.userid in src/main/resources/application.properties
	@Value("${ews.header.userid}")
	private String userId;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Override
	public void run(String... strings) throws Exception {
		try {
			//EwsClient ewsClient = new EwsClient();
			ewsClient.callEWS(SOAP_ADDRESS, SOAP_ACTION_MARKET_INFO, formRequest());
		} catch (SoapFaultClientException e) {
			log.error("Encountered a soap fault client exception");
			log.error(e.getFaultStringOrReason());
			Source sfceSource = e.getSoapFault().getSource();
			StringResult sr = new StringResult();
			TransformerFactory.newInstance().newTransformer().transform(sfceSource, sr);
			log.error(sr.toString());
		}
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


		requestHeader.setSource(sourceName);

		requestHeader.setUserID(userId);

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
