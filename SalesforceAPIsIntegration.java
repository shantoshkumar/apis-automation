/*
 *
 * File: SalesforceAPIsIntegration.java
 * Author: skumar
 */

package com.shantoshkumar

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.NodeList;

import java.io.IOException;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SalesforceAPIsIntegration {

  private static String sessionId = null;
  private static String endPoint = null;
  private static String accountId = null;
  private static CloseableHttpClient httpClient = null;
  private static ObjectMapper mapper = new ObjectMapper();

  // SAAJ - SOAP Client Testing
  public static void main(String args[]) throws Throwable {
    /*
     * The example below requests from the Web Service at:
     * http://www.webservicex.net/uszip.asmx?op=GetInfoByCity
     * 
     * 
     * To call other WS, change the parameters below, which are:
     * - the SOAP Endpoint URL (that is, where the service is responding from)
     * - the SOAP Action
     * 
     * Also change the contents of the method createSoapEnvelope() in this class. It constructs
     * the inner part of the SOAP envelope that is actually sent.
     */
    String soapEndpointUrl = "https://test.salesforce.com/services/Soap/u/48.0";
    String soapAction = "https://test.salesforce.com/services/Soap/u/48.0/login";

    callSoapWebService(soapEndpointUrl, soapAction);

    // Create a new Person Account(passing Lastname in request makes it a Person Account call)
    callCreatePersonAccount();
    // Using the session Id make next call to get the Person Account information
    callGetAccountInfo();
  }

  private static void callCreatePersonAccount() throws Throwable {

    /*
     * Sample JSON Request
     * {
     * "firstName":"Shantosh",
     * "lastName":"Auto3",
     * "FE_External_ID__c":"75E3B8A6-9673-400F-AA9C-BE518FC8EEFC::SELF",
     * "Plan_Owner__c":"0010R00000AoJlUQAV",
     * "Services_Eligible__c":"Branch Management; Subadvised PA;"
     * }
     * 
     */

    httpClient = HttpClients.createDefault();

    URIBuilder builder = new URIBuilder();
    builder.setScheme("https");
    builder.setHost(endPoint);
    builder.setPath("/services/data/v48.0/sobjects/Account");

    HttpPost request = new HttpPost(builder.build());
    request.addHeader("authorization", "Bearer " + sessionId);
    request.addHeader("accept", "application/json");
    request.addHeader("Content-Type", "application/json");

    SFPersonAccountDto body = new SFPersonAccountDto();
    body.setFirstName("Shantosh");
    body.setLastName("Auto4");
    body.setfe_external_id__c("1691BE2B-8C5B-499B-9902-D9880" + "::SELF");
    body.setplan_owner__c("000AoJlUQAV");
    body.setServices_Eligible__c("BM; SPA;");

    mapper = new ObjectMapper();
    String jsonBody = mapper.writeValueAsString(body);
    HttpEntity entity = new StringEntity(jsonBody);
    request.setEntity(entity);
    System.out.println(request);
    HttpResponse httpResponse = httpClient.execute(request);

    System.out.println("Create Account Response Code: " + httpResponse.getStatusLine().getStatusCode());
    String userResponse = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
    JsonNode userNode = mapper.readTree(userResponse);

    System.out.println("Create Account Response:" + userResponse);
    String id = userNode.findValue("id").toString();
    String[] acctId = id.split("\"");
    accountId = acctId[1];
    System.out.println("Created Id: " + accountId);


    try {
      httpClient.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static void callGetAccountInfo() throws Throwable {
    httpClient = HttpClients.createDefault();

    // String authValue = "Bearer " + sessionId;

    URIBuilder builder = new URIBuilder();
    builder.setScheme("https");
    builder.setHost(endPoint);
    // accountId = "0010R00000yyYIE";
    builder.setPath("/services/data/v48.0/sobjects/Account/" + accountId);

    HttpGet request = new HttpGet(builder.build());
    request.addHeader("authorization", "Bearer " + sessionId);
    System.out.println(request);
    HttpResponse httpResponse = httpClient.execute(request);

    System.out.println("Response Code: " + httpResponse.getStatusLine().getStatusCode());
    String userResponse = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
    JsonNode userNode = mapper.readTree(userResponse);

    System.out.println("Response:" + userResponse);
    System.out.println("User Name " + userNode.findValue("Name"));

    try {
      httpClient.close();
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  private static void createSoapEnvelope(SOAPMessage soapMessage) throws SOAPException {
    SOAPPart soapPart = soapMessage.getSOAPPart();

    String myNamespace = "n1";
    String myNamespaceURI = "urn:partner.soap.sforce.com";

    // SOAP Envelope
    SOAPEnvelope envelope = soapPart.getEnvelope();
    envelope.addNamespaceDeclaration(myNamespace, myNamespaceURI);

    /*
     * Equivalent to the below body(used in Postman Client)
     * <?xml version="1.0" encoding="utf-8" ?>
     * <env:Envelope xmlns:xsd="http://www.w3.org/2001/XMLSchema"
     * xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
     * xmlns:env="http://schemas.xmlsoap.org/soap/envelope/">
     * <env:Body>
     * <n1:login xmlns:n1="urn:partner.soap.sforce.com">
     * <n1:username>{{username}}</n1:username>
     * <n1:password>{{password}}{{secretToken}}</n1:password>
     * </n1:login>
     * </env:Body>
     * </env:Envelope>
     */

    // SOAP Body
    SOAPBody soapBody = envelope.getBody();
    SOAPElement soapBodyElem = soapBody.addChildElement("login", myNamespace);
    SOAPElement soapBodyElem1 = soapBodyElem.addChildElement("username", myNamespace);
    soapBodyElem1.addTextNode("myUserName");
    SOAPElement soapBodyElem2 = soapBodyElem.addChildElement("password", myNamespace);
    soapBodyElem2.addTextNode("MyUserPwdSecretToken");
  }

  private static void callSoapWebService(String soapEndpointUrl, String soapAction) {
    try {

      // Create SOAP Connection
      SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
      SOAPConnection soapConnection = soapConnectionFactory.createConnection();

      // Send SOAP Message to SOAP Server
      SOAPMessage soapResponse = soapConnection.call(createSOAPRequest(soapAction), soapEndpointUrl);

      // Print the SOAP Response
      System.out.println("Response SOAP Message:");
      soapResponse.writeTo(System.out);
      SOAPBody body = soapResponse.getSOAPBody();
      NodeList list = body.getElementsByTagName("result");
      for (int i = 0; i < list.getLength(); i++) {
        NodeList innerList = list.item(i).getChildNodes();

        for (int j = 0; j < innerList.getLength(); j++) {
          if (innerList.item(j).getNodeName().equals("sessionId")) {
            sessionId = innerList.item(j).getTextContent();
          }
          if (innerList.item(j).getNodeName().equals("serverUrl")) {
            String serverUrl = innerList.item(j).getTextContent();
            String[] url = serverUrl.split("/");
            endPoint = url[2];
          }
          // System.out.println(innerList.item(j).getNodeName());
          // System.out.println(innerList.item(j).getTextContent());
        }
      }
      System.out.println("\nSessionId: " + sessionId);
      System.out.println("EndPoint URL: " + endPoint);

      soapConnection.close();
    } catch (Exception e) {
      System.err.println(
          "\nError occurred while sending SOAP Request to Server!\nMake sure you have the correct endpoint URL and SOAPAction!\n");
      e.printStackTrace();
    }
  }

  private static SOAPMessage createSOAPRequest(String soapAction) throws Exception {
    MessageFactory messageFactory = MessageFactory.newInstance();
    SOAPMessage soapMessage = messageFactory.createMessage();

    createSoapEnvelope(soapMessage);

    MimeHeaders headers = soapMessage.getMimeHeaders();
    headers.addHeader("SOAPAction", soapAction);

    soapMessage.saveChanges();

    /* Print the request message, just for debugging purposes */
    System.out.println("Request SOAP Message:");
    soapMessage.writeTo(System.out);
    System.out.println("\n");

    return soapMessage;
  }

}


