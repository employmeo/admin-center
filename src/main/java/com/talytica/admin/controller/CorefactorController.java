package com.talytica.admin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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
}
