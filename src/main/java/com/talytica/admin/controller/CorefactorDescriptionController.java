package com.talytica.admin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.employmeo.data.model.Corefactor;
import com.employmeo.data.model.CorefactorDescription;
import com.employmeo.data.service.CorefactorDescriptionService;
import com.employmeo.data.service.CorefactorService;

@Controller
@RequestMapping("/admin/corefactorDescription")
public class CorefactorDescriptionController {

	@Autowired
	CorefactorDescriptionService corefactorDescriptionService;
	
	@Autowired
	CorefactorService corefactorService;

	private static final String FRAGMENT_ROOT = "model/";
	private static final String MODEL = "corefactorDescription";
	private static final String MODEL_DISPLAY = "Corefactor Description";
	private static final String LIST_VIEW = FRAGMENT_ROOT + MODEL + "/list";
	//private static final String CREATE_VIEW = FRAGMENT_ROOT + MODEL + "/create";
	private static final String EDIT_VIEW = FRAGMENT_ROOT + MODEL + "/edit";
	private static final String DISPLAY_VIEW = FRAGMENT_ROOT + MODEL + "/view";

    @RequestMapping(value = {"","list"}, method = RequestMethod.GET)
    public String  list(Model model) {
    	model.addAttribute("model", MODEL);
    	model.addAttribute("modelDisplay", MODEL_DISPLAY);
    	model.addAttribute("items", corefactorDescriptionService.getAll());
    	return LIST_VIEW;
    }

    @RequestMapping(value = "create", method = RequestMethod.GET)
    public String create(Model model){
    	model.addAttribute("model", MODEL);
    	model.addAttribute("modelDisplay", MODEL_DISPLAY);
    	model.addAttribute("item", new CorefactorDescription());
    	return EDIT_VIEW;
    }

    @RequestMapping(value = "save", method = RequestMethod.POST)
    public String save(CorefactorDescription corefactorDescription, Model model) {
    	CorefactorDescription saved = corefactorDescriptionService.save(corefactorDescription);
        return "redirect:/admin/" + MODEL + "/" + saved.getId();
    }

    @RequestMapping(value = "edit/{id}", method = RequestMethod.GET)
    public String edit(@PathVariable Long id, Model model){
    	model.addAttribute("model", MODEL);
    	model.addAttribute("modelDisplay", MODEL_DISPLAY);
        model.addAttribute("item", corefactorDescriptionService.getById(id));
        return EDIT_VIEW;
    }

    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public String view(@PathVariable Long id, Model model){
    	model.addAttribute("model", MODEL);
    	model.addAttribute("modelDisplay", MODEL_DISPLAY);
        model.addAttribute("item", corefactorDescriptionService.getById(id));
        return DISPLAY_VIEW;
    }

    @RequestMapping(value = "delete/{id}", method = RequestMethod.GET)
    public String delete(@PathVariable Long id, Model model){
    	corefactorDescriptionService.delete(id);
        return "redirect:/admin/" + MODEL ;
    }
    
    @ModelAttribute("allCorefactors")
    public Iterable<Corefactor> getCorefactors() {
        return corefactorService.getAllCorefactors();
    }
}
