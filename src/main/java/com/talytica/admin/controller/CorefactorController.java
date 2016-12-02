package com.talytica.admin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.employmeo.data.model.Corefactor;
import com.employmeo.data.service.CorefactorService;

@Controller
@RequestMapping("/admin/corefactor")
public class CorefactorController {

	@Autowired
	CorefactorService corefactorService;

	private static final String FRAGMENT_ROOT = "model/";
	private static final String MODEL = "corefactor";
	private static final String MODEL_DISPLAY = "Corefactor";
	private static final String LIST_VIEW = FRAGMENT_ROOT + MODEL + "/list";
	private static final String CREATE_VIEW = FRAGMENT_ROOT + MODEL + "/create";
	private static final String EDIT_VIEW = FRAGMENT_ROOT + MODEL + "/edit";
	private static final String DISPLAY_VIEW = FRAGMENT_ROOT + MODEL + "/view";

    @RequestMapping(value = {"","list"}, method = RequestMethod.GET)
    public String  list(Model model) {
    	model.addAttribute("model", MODEL);
    	model.addAttribute("modelDisplay", MODEL_DISPLAY);
    	model.addAttribute("items", corefactorService.getAllCorefactors());
    	return LIST_VIEW;
    }

    @RequestMapping(value = "create", method = RequestMethod.GET)
    public String create(Model model){
    	model.addAttribute("model", MODEL);
    	model.addAttribute("modelDisplay", MODEL_DISPLAY);
    	model.addAttribute("item", new Corefactor());
    	return EDIT_VIEW;
    }

    @RequestMapping(value = "save", method = RequestMethod.POST)
    public String save(Corefactor corefactor, Model model) {
    	Corefactor saved = corefactorService.save(corefactor);
        return "redirect:/admin/corefactor/" + saved.getId();
    }

    @RequestMapping(value = "edit/{id}")
    public String edit(@PathVariable Long id, Model model){
    	model.addAttribute("model", MODEL);
    	model.addAttribute("modelDisplay", MODEL_DISPLAY);
        model.addAttribute("item", corefactorService.findCorefactorById(id));
        return EDIT_VIEW;
    }

    @RequestMapping(value = "{id}")
    public String view(@PathVariable Long id, Model model){
    	model.addAttribute("model", MODEL);
    	model.addAttribute("modelDisplay", MODEL_DISPLAY);
        model.addAttribute("item", corefactorService.findCorefactorById(id));
        return DISPLAY_VIEW;
    }
}
