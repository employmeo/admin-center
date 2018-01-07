package com.talytica.admin.objects;

import java.util.List;

import javax.ws.rs.FormParam;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class AnalyticsRequest {

	public List<Long> benchmarkId;
	public Long targetId;
	public List<Long> positionId;
	public String exportType;
	public String name;
}
