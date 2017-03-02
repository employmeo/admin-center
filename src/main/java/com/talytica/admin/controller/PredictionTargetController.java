package com.talytica.admin.controller;

import java.lang.reflect.Field;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.employmeo.data.model.PredictionModel;
import com.employmeo.data.model.PredictionTarget;
import com.employmeo.data.service.PredictionModelService;


import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/admin/predictiontarget")
public class PredictionTargetController {

	@Autowired
	PredictionModelService predictionModelService;

	private static final String FRAGMENT_ROOT = "model/";
	private static final String MODEL = "predictiontarget";
	private static final String MODEL_DISPLAY = "Prediction Target";
	private static final String LIST_VIEW = FRAGMENT_ROOT + MODEL + "/list";
	//private static final String CREATE_VIEW = FRAGMENT_ROOT + MODEL + "/create";
	private static final String EDIT_VIEW = FRAGMENT_ROOT + MODEL + "/edit";
	private static final String DISPLAY_VIEW = FRAGMENT_ROOT + MODEL + "/view";
	private static final Class MODEL_CLASS= PredictionTarget.class;

    @RequestMapping(value = {"","/list"}, method = RequestMethod.GET)
    public String  list(Model model) {
    	model.addAttribute("model", MODEL);
    	model.addAttribute("modelDisplay", MODEL_DISPLAY);
    	model.addAttribute("items", predictionModelService.getAllPredictionTargets());
    	return LIST_VIEW;
    }
   
    @RequestMapping(value = "create", method = RequestMethod.GET)
    public String create(Model model){
    	model.addAttribute("model", MODEL);
    	model.addAttribute("modelDisplay", MODEL_DISPLAY);
    	model.addAttribute("item", new PredictionTarget());
    	return EDIT_VIEW;
    }
    
    @RequestMapping(value = "save", method = RequestMethod.POST)
    public String save(PredictionTarget target, Model model) {
    	PredictionTarget saved = predictionModelService.save(target);
        return "redirect:/admin/" + MODEL + "/" + saved.getPredictionTargetId();
    }

    @RequestMapping(value = "edit/{id}", method = RequestMethod.GET)
    public String edit(@PathVariable Long id, Model model){
    	model.addAttribute("model", MODEL);
    	model.addAttribute("modelDisplay", MODEL_DISPLAY);
        model.addAttribute("item", predictionModelService.getTargetById(id));
        return EDIT_VIEW;
    }
    
    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public String view(@PathVariable Long id, Model model){
    	model.addAttribute("model", MODEL);
    	model.addAttribute("modelDisplay", MODEL_DISPLAY);
        model.addAttribute("item", predictionModelService.getTargetById(id));
        return DISPLAY_VIEW;
    }
    
    @ModelAttribute("fieldnames")
    public Field[] getSurveySections() {  	
        return MODEL_CLASS.getDeclaredFields();
    }
}
