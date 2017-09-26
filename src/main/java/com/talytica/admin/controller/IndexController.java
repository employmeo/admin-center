package com.talytica.admin.controller;

import javax.ws.rs.FormParam;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.talytica.admin.objects.ServerStatus;
import com.talytica.common.service.ExternalLinksService;
import com.talytica.common.service.ServerAdminService;

@Controller
public class IndexController {

	@Autowired
	ServerAdminService serverAdminService;
	
	ExternalLinksService linksService;
	
    @RequestMapping("/admin/index")
    public String index(Model model){
    	ServerStatus statuses = new ServerStatus();
    	
    	JSONObject survey = serverAdminService.serverHealthCheck("survey");
    	statuses.setSurveyStatus(survey.optString("status", "unknown"));
    	if ("UP".equalsIgnoreCase(statuses.getSurveyStatus())) statuses.setSurvey(true);
    	
    	JSONObject portal = serverAdminService.serverHealthCheck("portal");
    	statuses.setPortalStatus(portal.optString("status", "unknown"));
    	if ("UP".equalsIgnoreCase(statuses.getPortalStatus())) statuses.setPortal(true);

    	JSONObject integration = serverAdminService.serverHealthCheck("integration");
    	statuses.setIntegrationStatus(integration.optString("status", "unknown"));
    	if ("UP".equalsIgnoreCase(statuses.getIntegrationStatus())) statuses.setIntegration(true);

    	model.addAttribute("item", statuses);
    	return "dashboard";
    }
    
    @RequestMapping("/admin/index/reset/{serverName}")
    public String clearcaches(Model model, @PathVariable String serverName){

    	serverAdminService.clearRemoteCache(serverName);
    	model.addAttribute("message", serverName + " server(s) cache cleared" );
        return index(model);
    }

    @RequestMapping("/admin/index/trigger")
    public String trigger(Model model, @FormParam(value="action") String action){
    	serverAdminService.triggerPipeline(action);
    	model.addAttribute("message", action + " pipeline process(es) triggered" );
        return index(model);
    }
    
}
