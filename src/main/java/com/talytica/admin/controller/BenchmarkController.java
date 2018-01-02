package com.talytica.admin.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.FormParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.apache.commons.lang3.StringEscapeUtils;
import com.employmeo.data.model.Benchmark;
import com.employmeo.data.model.Corefactor;
import com.employmeo.data.model.Criterion;
import com.employmeo.data.model.Outcome;
import com.employmeo.data.model.Respondant;
import com.employmeo.data.model.RespondantNVP;
import com.employmeo.data.model.RespondantScore;
import com.employmeo.data.model.Response;
import com.employmeo.data.repository.PredictionTargetRepository;
import com.employmeo.data.service.AccountService;
import com.employmeo.data.service.CorefactorService;
import com.employmeo.data.service.RespondantService;
import com.google.common.collect.Lists;
import com.talytica.common.service.AnalyticsExtractionService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/admin/benchmark")
public class BenchmarkController {

	@Autowired
	AccountService accountService;
	
	@Autowired
	RespondantService respondantService;
	
	@Autowired
	AnalyticsExtractionService analyticsExtractionService;

	@Autowired
	PredictionTargetRepository predictionTargetRepository;
	
	@Autowired
	CorefactorService corefactorService;

	private static final String FRAGMENT_ROOT = "model/";
	private static final String MODEL = "benchmark";
	private static final String MODEL_DISPLAY = "Benchmark";
	private static final String LIST_VIEW = FRAGMENT_ROOT + MODEL + "/list";
	//private static final String CREATE_VIEW = FRAGMENT_ROOT + MODEL + "/create";
	private static final String EDIT_VIEW = FRAGMENT_ROOT + MODEL + "/edit";
	private static final String DISPLAY_VIEW = FRAGMENT_ROOT + MODEL + "/view";
	private static final Class MODEL_CLASS= Benchmark.class;

	private static final String DELIMITER = ",";
	private static final String NEWLINE = "\n";
	private static final String EXPORT_FILENAME = "export.csv";

    @RequestMapping(value = {"","/list"}, method = RequestMethod.GET)
    public String  list(Model model) {
    	model.addAttribute("model", MODEL);
    	model.addAttribute("modelDisplay", MODEL_DISPLAY);
    	model.addAttribute("items", accountService.getAllBenchmarks());
    	return LIST_VIEW;
    }
   
    @RequestMapping(value = "create", method = RequestMethod.GET)
    public String create(Model model){
    	model.addAttribute("model", MODEL);
    	model.addAttribute("modelDisplay", MODEL_DISPLAY);
    	model.addAttribute("item", new Benchmark());
    	return EDIT_VIEW;
    }
    
    @RequestMapping(value = "save", method = RequestMethod.POST)
    public String save(Benchmark benchmark, Model model) {
    	Benchmark saved = accountService.save(benchmark);
        return "redirect:/admin/" + MODEL + "/" + saved.getId();
    }

    @RequestMapping(value = "edit/{id}", method = RequestMethod.GET)
    public String edit(@PathVariable Long id, Model model){
    	model.addAttribute("model", MODEL);
    	model.addAttribute("modelDisplay", MODEL_DISPLAY);
        model.addAttribute("item", accountService.getBenchmarkById(id));
        return EDIT_VIEW;
    }
    
    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public String view(@PathVariable Long id, Model model){
    	model.addAttribute("model", MODEL);
    	model.addAttribute("modelDisplay", MODEL_DISPLAY);
        model.addAttribute("item", accountService.getBenchmarkById(id));
        model.addAttribute("targetList", predictionTargetRepository.findAll());
        return DISPLAY_VIEW;
    }
    
    
    @RequestMapping(value = "export/{id}", method = RequestMethod.POST)
    public void export(@PathVariable Long id, @FormParam("targetId") Long targetId, Model model, HttpServletResponse response) throws Exception{
        String csvFileName = EXPORT_FILENAME;
        response.setContentType("text/csv");
        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=\"%s\"", csvFileName);
        response.setHeader(headerKey, headerValue);
        
        File tempfile = analyticsExtractionService.extractBenchmarkDataWithOutcome(id, targetId);     
        InputStream is = new FileInputStream(tempfile);
        org.apache.commons.io.IOUtils.copy(is, response.getOutputStream());
        response.flushBuffer();
    }
      
    @ModelAttribute("fieldnames")
    public Field[] getFieldNames() {  	
        return MODEL_CLASS.getDeclaredFields();
    }
}
