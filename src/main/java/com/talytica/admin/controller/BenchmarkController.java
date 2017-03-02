package com.talytica.admin.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.employmeo.data.model.Benchmark;
import com.employmeo.data.model.Corefactor;
import com.employmeo.data.model.Outcome;
import com.employmeo.data.model.Respondant;
import com.employmeo.data.model.RespondantScore;
import com.employmeo.data.service.AccountService;
import com.employmeo.data.service.CorefactorService;
import com.employmeo.data.service.RespondantService;

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
	CorefactorService corefactorService;

	private static final String FRAGMENT_ROOT = "model/";
	private static final String MODEL = "answer";
	private static final String MODEL_DISPLAY = "Answer";
	private static final String LIST_VIEW = FRAGMENT_ROOT + MODEL + "/list";
	//private static final String CREATE_VIEW = FRAGMENT_ROOT + MODEL + "/create";
	private static final String EDIT_VIEW = FRAGMENT_ROOT + MODEL + "/edit";
	private static final String DISPLAY_VIEW = FRAGMENT_ROOT + MODEL + "/view";

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
        return DISPLAY_VIEW;
    }
    
    @RequestMapping(value = "export/{id}/{targetId}", method = RequestMethod.GET)
    public void export(@PathVariable Long id, @PathVariable Long targetId, Model model, HttpServletResponse response) throws IOException{
    	
        String csvFileName = EXPORT_FILENAME;    
        response.setContentType("text/csv");
        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=\"%s\"",
                csvFileName);
        response.setHeader(headerKey, headerValue);
        PrintWriter fileWriter = response.getWriter();
        Set<Respondant> respondants = respondantService.getCompletedForBenchmarkId(id);
        log.debug("Found {} respondants", respondants.size());
        String header = null;
        List<Long> headers = new ArrayList<Long>();

        for (Respondant respondant : respondants) {
	       	Set<RespondantScore> scores = respondant.getRespondantScores();
	       	Set<Outcome> outcomes = respondantService.getOutcomesForRespondant(respondant.getId());
	       	if (header == null) {
	       		StringBuffer sbHeader = new StringBuffer();
	       		sbHeader.append("respondant_id");
	       		for (RespondantScore score : scores) {
	       			Corefactor corefactor = corefactorService.findCorefactorById(score.getId().getCorefactorId());
	       			sbHeader.append(DELIMITER);
	       			sbHeader.append(corefactor.getName());
	       			headers.add(corefactor.getId());
	       		}
        		sbHeader.append(DELIMITER);
        		sbHeader.append("OUTCOME");
        		sbHeader.append(NEWLINE);
        		header = sbHeader.toString();
        		log.debug("WRITING: {}",header);
	           	fileWriter.append(header);        		
	       	}
	       	StringBuffer lineItem = new StringBuffer();
	       	lineItem.append(respondant.getId());
	       	for (Long cfid : headers) {
        		lineItem.append(DELIMITER);
        		lineItem.append(findCorefactorValue(cfid, scores));
	       	}
    		lineItem.append(DELIMITER);
    		lineItem.append(findOutcomeValue(targetId, outcomes));
    		lineItem.append(NEWLINE);
    		log.debug("WRITING: {}",lineItem.toString());
    		fileWriter.append(lineItem.toString());	
        }   
    }
      
    private String findCorefactorValue(Long id, Set<RespondantScore> scores) {
    	Optional<RespondantScore> value = scores.stream().filter(cfs -> id.equals(cfs.getId().getCorefactorId())).findFirst();
    	if (value.isPresent()) {
    		return value.get().getValue().toString();
    	}
    	return "";
    }
    
    private String findOutcomeValue(Long id, Set<Outcome> outcomes) {
    	Optional<Outcome> value = outcomes.stream().filter(outcome -> id.equals(outcome.getPredictionTarget().getPredictionTargetId())).findFirst();
    	if (value.isPresent()) {
    		if (value.get().getValue()) return "1";
    		return "0";
    	}
    	return "";
    }
}
