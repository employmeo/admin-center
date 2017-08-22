package com.talytica.admin.objects;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ServerStatus {

	Boolean survey;
	Boolean integration;
	Boolean portal;

	String surveyStatus;
	String integrationStatus;
	String portalStatus;
	
}
