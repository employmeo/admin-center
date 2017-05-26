package com.talytica.admin.controller;

import java.lang.reflect.Field;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.employmeo.data.model.Account;
import com.employmeo.data.model.Criterion;
import com.employmeo.data.model.Position;
import com.employmeo.data.model.PositionPredictionConfiguration;
import com.employmeo.data.model.PredictionModel;
import com.employmeo.data.repository.CriterionRepository;
import com.employmeo.data.service.AccountService;
import com.employmeo.data.service.PredictionConfigurationService;
import com.employmeo.data.service.PredictionModelService;


import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/admin/positionconfig")
public class PositionConfigController {

	@Autowired
	AccountService accountService;
	
	@Autowired
	PredictionConfigurationService predictionConfigurationService;
	

	private static final String FRAGMENT_ROOT = "model/";
	private static final String MODEL = "positionconfig";
	private static final String MODEL_DISPLAY = "Position Prediction Configuration";
	private static final String LIST_VIEW = FRAGMENT_ROOT + MODEL + "/list";
	//private static final String CREATE_VIEW = FRAGMENT_ROOT + MODEL + "/create";
	private static final String EDIT_VIEW = FRAGMENT_ROOT + MODEL + "/edit";
	private static final String DISPLAY_VIEW = FRAGMENT_ROOT + MODEL + "/view";
	private static final Class MODEL_CLASS= PositionPredictionConfiguration.class;

    @RequestMapping(value = {"","/list"}, method = RequestMethod.GET)
    public String  list(Model model) {
    	model.addAttribute("model", MODEL);
    	model.addAttribute("modelDisplay", MODEL_DISPLAY);
    	model.addAttribute("items", predictionConfigurationService.getAllPositionPredictionConfigurations());
    	return LIST_VIEW;
    }
   
    @RequestMapping(value = "create", method = RequestMethod.GET)
    public String create(Model model){
    	model.addAttribute("model", MODEL);
    	model.addAttribute("modelDisplay", MODEL_DISPLAY);
    	model.addAttribute("item", new PositionPredictionConfiguration());
    	return EDIT_VIEW;
    }
    
    @RequestMapping(value = "save", method = RequestMethod.POST)
    public String save(PositionPredictionConfiguration config, Model model) {
    	PositionPredictionConfiguration saved = predictionConfigurationService.save(config);
        return "redirect:/admin/" + MODEL + "/" + saved.getPositionPredictionConfigId();
    }

    @RequestMapping(value = "edit/{id}", method = RequestMethod.GET)
    public String edit(@PathVariable Long id, Model model){
    	model.addAttribute("model", MODEL);
    	model.addAttribute("modelDisplay", MODEL_DISPLAY);
        model.addAttribute("item", predictionConfigurationService.getPositionPredictionConfigurationById(id));
        return EDIT_VIEW;
    }
    
    @RequestMapping(value = "delete/{id}", method = RequestMethod.GET)
    public String delete(@PathVariable Long id, Model model){
    	model.addAttribute("model", MODEL);
    	model.addAttribute("modelDisplay", MODEL_DISPLAY);
    	//predictionConfigurationService.delete(predictionConfigurationService.getPositionPredictionConfigurationById(id));
    	model.addAttribute("items", predictionConfigurationService.getAllPositionPredictionConfigurations());
        return LIST_VIEW;
    }
    
    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public String view(@PathVariable Long id, Model model){
    	model.addAttribute("model", MODEL);
    	model.addAttribute("modelDisplay", MODEL_DISPLAY);
        model.addAttribute("item", predictionConfigurationService.getPositionPredictionConfigurationById(id));
        return DISPLAY_VIEW;
    }
     
    @ModelAttribute("fieldnames")
    public Field[] getFieldNames() {  	
        return MODEL_CLASS.getDeclaredFields();
    }
}
