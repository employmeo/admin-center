package com.talytica.admin.controller;

import java.lang.reflect.Field;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.employmeo.data.model.ReferenceCheckConfig;
import com.employmeo.data.repository.ReferenceCheckConfigRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/admin/rcconfig")
public class RCConfigController {

	@Autowired
	ReferenceCheckConfigRepository rcconfigRepository;

	private static final String FRAGMENT_ROOT = "model/";
	private static final String MODEL = "rcconfig";
	private static final String MODEL_DISPLAY = "Ref Check Config";
	private static final String LIST_VIEW = FRAGMENT_ROOT + "generic/list";
	private static final String EDIT_VIEW = FRAGMENT_ROOT + "generic/edit";
	private static final String DISPLAY_VIEW = FRAGMENT_ROOT + "generic/view";
	private static final Class MODEL_CLASS= ReferenceCheckConfig.class;

    @RequestMapping(value = {"","/list"}, method = RequestMethod.GET)
    public String  list(Model model) {
    	model.addAttribute("model", MODEL);
    	model.addAttribute("modelDisplay", MODEL_DISPLAY);
    	model.addAttribute("items", rcconfigRepository.findAll());
    	return LIST_VIEW;
    }
   
    @RequestMapping(value = "create", method = RequestMethod.GET)
    public String create(Model model){
    	model.addAttribute("model", MODEL);
    	model.addAttribute("modelDisplay", MODEL_DISPLAY);
    	model.addAttribute("item", new ReferenceCheckConfig());
    	return EDIT_VIEW;
    }
    
    @RequestMapping(value = "save", method = RequestMethod.POST)
    public String save(ReferenceCheckConfig rcconfig, Model model) {
    	ReferenceCheckConfig saved = rcconfigRepository.save(rcconfig);
        return "redirect:/admin/" + MODEL + "/" + saved.getId();
    }

    @RequestMapping(value = "edit/{id}", method = RequestMethod.GET)
    public String edit(@PathVariable Long id, Model model){
    	model.addAttribute("model", MODEL);
    	model.addAttribute("modelDisplay", MODEL_DISPLAY);
        model.addAttribute("item", rcconfigRepository.findOne(id));
        return EDIT_VIEW;
    }
    
    @RequestMapping(value = "delete/{id}", method = RequestMethod.GET)
    public String delete(@PathVariable Long id, Model model){
    	model.addAttribute("model", MODEL);
    	model.addAttribute("modelDisplay", MODEL_DISPLAY);
    	rcconfigRepository.delete(rcconfigRepository.findOne(id));
    	model.addAttribute("items", rcconfigRepository.findAll());
        return LIST_VIEW;
    }
    
    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public String view(@PathVariable Long id, Model model){
    	model.addAttribute("model", MODEL);
    	model.addAttribute("modelDisplay", MODEL_DISPLAY);
        model.addAttribute("item", rcconfigRepository.findOne(id));
        return DISPLAY_VIEW;
    }
     
    @ModelAttribute("fieldnames")
    public Field[] getFieldNames() {  	
        return MODEL_CLASS.getDeclaredFields();
    }
}
