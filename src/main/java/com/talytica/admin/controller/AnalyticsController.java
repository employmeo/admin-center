package com.talytica.admin.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.FormParam;
import javax.ws.rs.PathParam;

import org.bigml.binding.BigMLClient;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.employmeo.data.model.Account;
import com.employmeo.data.model.Benchmark;
import com.employmeo.data.model.Position;
import com.employmeo.data.model.PredictionTarget;
import com.employmeo.data.model.Respondant;
import com.employmeo.data.service.AccountService;
import com.employmeo.data.service.PredictionModelService;
import com.employmeo.data.service.RespondantService;
import com.talytica.admin.objects.AnalyticsRequest;
import com.talytica.common.service.AnalyticsExtractionService;
import com.talytica.common.service.StorageService;

import jersey.repackaged.com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/admin/analytics")
public class AnalyticsController {
	// save uploaded file to this folder
	
	@Value("${com.talytica.temp.directory:C://users//sri//workspace//temp//}")
	private String TEMP_FOLDER;
	
	@Value("${com.talytica.apis.bigml.name}")
	private String userName;
	@Value("${com.talytica.apis.bigml.key}")
	private String apiKey;
	@Value("${com.talytica.apis.bigml.devmode:true}")
	private Boolean devMode;
	
	private static final String FRAGMENT_ROOT = "model/";
	private static final String MODEL = "analytics";
	private static final String MODEL_DISPLAY = "Analytics";
	private static final String LIST_VIEW = FRAGMENT_ROOT + MODEL + "/list";
	private final ExecutorService TASK_EXECUTOR = Executors.newCachedThreadPool();
	
	@Autowired
	StorageService storageService;
	@Autowired
	AnalyticsExtractionService analyticsExtractionService;
	@Autowired
	AccountService accountService;
	@Autowired
	PredictionModelService predictionModelService;
	@Autowired
	RespondantService respondantService;
	
	
    @RequestMapping(value = {"","/","index"}, method = RequestMethod.GET)
    public String list(Model model) {
    	return LIST_VIEW;
    }
	
    @RequestMapping(value = "/", method = RequestMethod.POST)
    public String export(@FormParam("request") AnalyticsRequest request, Model model) throws Exception {
    	
    	List<Respondant> respondants = new ArrayList<Respondant>();
    	Account account;
    	log.debug("ids: {}", request);
    	switch (request.getExportType()) {
	    	case "benchmark":
	    		Benchmark bm = accountService.getBenchmarkById(request.getBenchmarkId().get(0));
	    		account = accountService.getAccountById(bm.getAccountId());
	    		respondants.addAll(respondantService.getByBenchmarkId(bm.getId()));
	    		if ((request.getName() == null) || (request.getName().isEmpty())) request.setName(account.getAccountName() + bm.getPosition().getPositionName() + "-benchmark.csv");  
	    		break;
	    	case "position":
	    		Position pos = accountService.getPositionById(request.getPositionId().get(0));
	    		account = pos.getAccount();
	    		respondants.addAll(respondantService.getScoredApplicantsByPosition(request.getPositionId().get(0)));
	    		if ((request.getName() == null) || (request.getName().isEmpty())) request.setName(account.getAccountName() + pos.getPositionName() + "-position.csv");  
	    		break;
	    	case "blended":
	    		for (Long benchmarkId : request.getBenchmarkId()) {
	    			respondants.addAll(respondantService.getByBenchmarkId(benchmarkId));
	    		}
	    		for (Long positionId : request.getPositionId()) {
	    			respondants.addAll(respondantService.getScoredApplicantsByPosition(positionId));
	    		}
	    		if ((request.getName() == null) || (request.getName().isEmpty())) request.setName("blended.csv");
	    		break;
	    	default:
	    		model.addAttribute("alert", "No File Created");
	    		return list(model);
    	}
    	  	
    	String fileName = request.getName().replace(" ", "_");
    	exportRespondantsToS3(fileName, respondants, request.getTargetId());
        model.addAttribute("alert", fileName + " created with " + respondants.size() + " records.");   
        return list(model);
    }
    
    private void exportRespondantsToS3(String fileName, List<Respondant> respondants, Long targetId) throws Exception {
    	TASK_EXECUTOR.submit(new Runnable() {
				@Override
				public void run() {
					try {
				    	log.debug("Writing {} respondants to {}", respondants.size(), fileName);
				        File tempfile = analyticsExtractionService.extractAllDataWithOutcome(respondants, targetId);
				    	log.debug("Pushing tempfile {} to Amazon S3 as {}", tempfile.getName(), fileName);
				        storageService.uploadAnalyticsFile(tempfile, fileName);
				        log.debug("deleteing tempfile: {}", tempfile.getName());
				        tempfile.delete();
					} catch (Exception e) {
						log.error("Unable to complete analytics extract request", e);
					}
				}
    		});
    }
    
    @RequestMapping(value = "/push", method = RequestMethod.POST)
    public String pushToBigML(@FormParam("key") String key, Model model) throws Exception {
    	BigMLClient api = BigMLClient.getInstance(userName,apiKey,devMode);
    	String url = storageService.getS3Link(key);
    	JSONObject args = new JSONObject();
    	JSONObject parser = new JSONObject();
    	JSONObject source = api.createRemoteSource(url, parser ,args);
    	try {
    		JSONObject object = (JSONObject) source.get("object");
    		JSONObject status = (JSONObject) object.get("status");
    		model.addAttribute("alert", status.get("message").toString());
    	} catch (Exception e) {
    		model.addAttribute("alert", source.toString());
    	} 	
        return list(model);
    }
    
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public String deleteFromS3(@FormParam("key") String key, Model model) throws Exception {
    	storageService.deleteFile(key);
        return list(model);
    }
    
    @ModelAttribute("allFiles")
    public Iterable<S3ObjectSummary> getSurveySections() {
    	Iterable<S3ObjectSummary> summaries = storageService.getDirectoryList(storageService.getEnvPath() + "/analytics/");
        return summaries;
    }
    
    @ModelAttribute("allBenchmarks")
    public Iterable<Benchmark> getBenchmarks() {  	
        return accountService.getAllBenchmarks();
    }
    
    @ModelAttribute("allPositions")
    public Iterable<Position> getPositions() {
        return accountService.getAllPositions();
    }
    
    @ModelAttribute("allTargets")
    public Iterable<PredictionTarget> getTargets() {
    	return predictionModelService.getAllPredictionTargets();
    }
}
