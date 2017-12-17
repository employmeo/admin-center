package com.talytica.admin.controller;

import java.lang.reflect.Field;
import java.util.List;

import javax.ws.rs.FormParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.employmeo.data.model.Respondant;
import com.employmeo.data.service.RespondantService;

import jersey.repackaged.com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/admin/respondant")
public class RespondantErrorsController {

	@Autowired
	RespondantService respondantService;

	private static final String FRAGMENT_ROOT = "model/";
	private static final String MODEL = "respondant";
	private static final String MODEL_DISPLAY = "Error Status Respondants";
	private static final String LIST_VIEW = FRAGMENT_ROOT + "respondant/list";
	private static final String EDIT_VIEW = FRAGMENT_ROOT + "generic/edit";
	private static final String DISPLAY_VIEW = FRAGMENT_ROOT + "generic/view";
	private static final Class MODEL_CLASS= Respondant.class;

    @RequestMapping(value = {"","/list"}, method = RequestMethod.GET)
    public String  list(Model model) {
    	model.addAttribute("model", MODEL);
    	model.addAttribute("modelDisplay", MODEL_DISPLAY);
    	List<Integer> statuses = Lists.newArrayList();
    	model.addAttribute("items", respondantService.getErrorStatusRespondants(null, statuses, true, 1));
    	return LIST_VIEW;
    }
   
    @RequestMapping(value = {"","/list"}, method = RequestMethod.POST)
    public String  search(Model model, 
    		@FormParam("accountId") Long accountId, 
    		@FormParam("statuses") List<Integer> statuses, 
    		@FormParam("errorStatus") Boolean errorStatus,
    		@FormParam("page") Integer page ) {
    	if (page == null) page = 1;
    	if (errorStatus == null) errorStatus = true;
    	if (statuses == null) statuses = Lists.newArrayList();
    	
    	model.addAttribute("model", MODEL);
    	model.addAttribute("modelDisplay", MODEL_DISPLAY);
    	model.addAttribute("items", respondantService.getErrorStatusRespondants(accountId, statuses, errorStatus, page));
    	return LIST_VIEW;
    }
    
    @RequestMapping(value = "reset", method = RequestMethod.POST)
    public Integer reset(@RequestBody List<String> respondantIdStrings, Model model){
    	log.debug("Resetting {}", respondantIdStrings);
    	if (!respondantIdStrings.isEmpty()) {
	    	List<Long> respondantIds = Lists.newArrayList(); 
	    	for (String id : respondantIdStrings) respondantIds.add(Long.valueOf(id));
	    	respondantService.clearErrors(respondantIds);
    	}
        return respondantIdStrings.size();
    }

    @RequestMapping(value = "save", method = RequestMethod.POST)
    public String save(Respondant respondant, Model model) {
    	Respondant saved = respondantService.save(respondant);
        return "redirect:/admin/" + MODEL + "/" + saved.getId();
    }

    @RequestMapping(value = "edit/{id}", method = RequestMethod.GET)
    public String edit(@PathVariable Long id, Model model){
    	model.addAttribute("model", MODEL);
    	model.addAttribute("modelDisplay", MODEL_DISPLAY);
        model.addAttribute("item", respondantService.getRespondantById(id));
        return EDIT_VIEW;
    }
    
    
    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public String view(@PathVariable Long id, Model model){
    	model.addAttribute("model", MODEL);
    	model.addAttribute("modelDisplay", MODEL_DISPLAY);
        model.addAttribute("item", respondantService.getRespondantById(id));
        return DISPLAY_VIEW;
    }
     
    @ModelAttribute("fieldnames")
    public Field[] getFieldNames() {  	
        return MODEL_CLASS.getDeclaredFields();
    }
}
