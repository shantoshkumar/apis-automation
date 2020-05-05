/*
 *
 * File: SFPersonAccountDto.java
 * Author: skumar
 */

package com.shantoshkumar;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fngn.framework.rest.jsonview.JsonViews;

public class SFPersonAccountDto implements Serializable {
  private static final long serialVersionUID = 1L;
  public static final String FIRST_NAME = "firstName";
  public static final String LAST_NAME = "lastName";
  public static final String EXTERNAL_ID = "external_id__c";
  public static final String PLAN_OWNER = "plan_owner__c";
  public static final String SERVICES_ELIGIBLE = "Services_Eligible__c";

  private String firstName;
  private String lastName;
  private String external_id__c;
  private String plan_owner__c;
  private String Services_Eligible__c;

  @JsonProperty(FIRST_NAME)
  @JsonView(JsonViews.DefaultFullyAuthenticated.class)
  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  @JsonProperty(LAST_NAME)
  @JsonView(JsonViews.DefaultFullyAuthenticated.class)
  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  @JsonProperty(EXTERNAL_ID)
  @JsonView(JsonViews.DefaultFullyAuthenticated.class)
  public String getexternal_id__c() {
    return external_id__c;
  }

  public void setexternal_id__c(String external_id__c) {
    this.external_id__c = external_id__c;
  }

  @JsonProperty(PLAN_OWNER)
  @JsonView(JsonViews.DefaultFullyAuthenticated.class)
  public String getplan_owner__c() {
    return plan_owner__c;
  }

  @JsonProperty(SERVICES_ELIGIBLE)
  @JsonView(JsonViews.DefaultFullyAuthenticated.class)
  public void setplan_owner__c(String plan_owner__c) {
    this.plan_owner__c = plan_owner__c;
  }

  public String getServices_Eligible__c() {
    return Services_Eligible__c;
  }

  public void setServices_Eligible__c(String services_Eligible__c) {
    Services_Eligible__c = services_Eligible__c;
  }

}
