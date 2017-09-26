package com.talytica.admin.controller;

import java.lang.reflect.Field;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.employmeo.data.model.Account;
import com.employmeo.data.model.Criterion;
import com.employmeo.data.model.CustomWorkflow;
import com.employmeo.data.model.PredictionModel;
import com.employmeo.data.model.User;
import com.employmeo.data.repository.CriterionRepository;
import com.employmeo.data.service.AccountService;
import com.employmeo.data.service.PredictionModelService;
import com.employmeo.data.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/admin/workflow")
public class CustomWorkflowController {

	@Autowired
	AccountService accountService;

	private static final String FRAGMENT_ROOT = "model/";
	private static final String MODEL = "workflow";
	private static final String MODEL_DISPLAY = "ATS Workflow Configuration";
	private static final String LIST_VIEW = FRAGMENT_ROOT + MODEL + "/list";
	//private static final String CREATE_VIEW = FRAGMENT_ROOT + MODEL + "/create";
	private static final String EDIT_VIEW = FRAGMENT_ROOT + MODEL + "/edit";
	private static final String DISPLAY_VIEW = FRAGMENT_ROOT + MODEL + "/view";
	private static final Class MODEL_CLASS= CustomWorkflow.class;

    @RequestMapping(value = {"","/list"}, method = RequestMethod.GET)
    public String  list(Model model) {
    	model.addAttribute("model", MODEL);
    	model.addAttribute("modelDisplay", MODEL_DISPLAY);
    	model.addAttribute("items", accountService.getAllWorkflows());
    	return LIST_VIEW;
    }
   
    @RequestMapping(value = "create", method = RequestMethod.GET)
    public String create(Model model){
    	model.addAttribute("model", MODEL);
    	model.addAttribute("modelDisplay", MODEL_DISPLAY);
    	model.addAttribute("item", new CustomWorkflow());
    	return EDIT_VIEW;
    }
    
    @RequestMapping(value = "save", method = RequestMethod.POST)
    public String save(CustomWorkflow workflow, Model model) {
    	CustomWorkflow saved = accountService.save(workflow);
        return "redirect:/admin/" + MODEL + "/" + saved.getId();
    }

    @RequestMapping(value = "edit/{id}", method = RequestMethod.GET)
    public String edit(@PathVariable Long id, Model model){
    	model.addAttribute("model", MODEL);
    	model.addAttribute("modelDisplay", MODEL_DISPLAY);
        model.addAttribute("item", accountService.getCustomWorkflowById(id));
        return EDIT_VIEW;
    }
    
    @RequestMapping(value = "delete/{id}", method = RequestMethod.GET)
    public String delete(@PathVariable Long id, Model model){
    	model.addAttribute("model", MODEL);
    	model.addAttribute("modelDisplay", MODEL_DISPLAY);
    	accountService.delete(accountService.getCustomWorkflowById(id));
    	model.addAttribute("items",accountService.getAllWorkflows());
        return LIST_VIEW;
    }
    
    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public String view(@PathVariable Long id, Model model){
    	model.addAttribute("model", MODEL);
    	model.addAttribute("modelDisplay", MODEL_DISPLAY);
        model.addAttribute("item", accountService.getCustomWorkflowById(id));
        return DISPLAY_VIEW;
    }
     
    @ModelAttribute("fieldnames")
    public Field[] getFieldNames() {  	
        return MODEL_CLASS.getDeclaredFields();
    }
}
