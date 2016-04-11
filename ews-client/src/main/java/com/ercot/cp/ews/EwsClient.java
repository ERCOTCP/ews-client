package com.ercot.cp.ews;

import com.ercot.schema._2007_06.nodal.ews.message.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.client.SoapFaultClientException;
import org.springframework.ws.soap.client.core.SoapActionCallback;

public class EwsClient extends WebServiceGatewaySupport {

	private static final Logger log = LoggerFactory.getLogger(EwsClient.class);

	public ResponseMessage callEWS(final String soapAddress, final String soapAction, RequestMessage input) 
			throws SoapFaultClientException {
		
		log.info("Calling EWS");
		log.info("Soap Address: " + soapAddress);
		log.info("Soap Action: " + soapAction);
		
		ResponseMessage response = (ResponseMessage) getWebServiceTemplate()
				.marshalSendAndReceive(
						soapAddress,
						input,
						new SoapActionCallback(soapAction));
		
		printResponseMessage(response);
		
		return response;
	}
	
	public void printResponseMessage(ResponseMessage response) {
		if(response != null) {
			//print response header
			HeaderType ht = response.getHeader();
			if(ht != null) {
				log.info("Verb: " + ht.getVerb());
				log.info("Noun: " + ht.getNoun());
				log.info("Revision: " + ht.getRevision());
				log.info("Source: " + ht.getSource());
				log.info("UserID: " + ht.getUserID());
				log.info("MessageID: " + ht.getMessageID());
			} else {
				log.info("Response header was null");
			}
			
			//print response reply
			ReplyType rt = response.getReply();
			if(rt != null) {
				log.info("ReplyCode: " + rt.getReplyCode());
				log.info("Timestamp: " + rt.getTimestamp().toString());
			} else {
				log.info("Response reply was null");
			}	
		} else {
			log.info("No response received");
		}
	}

}
