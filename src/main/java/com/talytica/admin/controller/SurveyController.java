package com.talytica.admin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.employmeo.data.model.Corefactor;
import com.employmeo.data.model.Survey;
import com.employmeo.data.model.SurveyQuestion;
import com.employmeo.data.model.SurveySection;
import com.employmeo.data.service.CorefactorService;
import com.employmeo.data.service.SurveyService;

@Controller
@RequestMapping("/admin/survey")
public class SurveyController {

	@Autowired
	SurveyService surveyService;

	private static final String FRAGMENT_ROOT = "model/";
	private static final String MODEL = "survey";
	private static final String SECTION_MODEL = "survey/surveySection";
	private static final String QUESTION_MODEL = "survey/surveyQuestion";
	private static final String MODEL_DISPLAY = "Survey";
	private static final String SECTION_MODEL_DISPLAY = "SurveySection";
	private static final String QUESTION_MODEL_DISPLAY = "SurveyQuestion";
	private static final String LIST_VIEW = FRAGMENT_ROOT + MODEL + "/list";
	private static final String CREATE_VIEW = FRAGMENT_ROOT + MODEL + "/create";
	private static final String EDIT_VIEW = FRAGMENT_ROOT + MODEL + "/edit";
	private static final String DISPLAY_VIEW = FRAGMENT_ROOT + MODEL + "/view";

    @RequestMapping(value = {"","list"}, method = RequestMethod.GET)
    public String  list(Model model) {
    	model.addAttribute("model", MODEL);
    	model.addAttribute("modelDisplay", MODEL_DISPLAY);
    	model.addAttribute("items", surveyService.getAllSurveys());
    	return LIST_VIEW;
    }

    @RequestMapping(value = "create", method = RequestMethod.GET)
    public String create(Model model){
    	model.addAttribute("model", MODEL);
    	model.addAttribute("modelDisplay", MODEL_DISPLAY);
    	model.addAttribute("item", new Survey());
    	return EDIT_VIEW;
    }

    @RequestMapping(value = "save", method = RequestMethod.POST)
    public String save(Survey survey, Model model) {
    	Survey saved = surveyService.save(survey);
        return "redirect:/admin/" + MODEL + "/" + saved.getId();
    }

    @RequestMapping(value = "edit/{id}", method = RequestMethod.GET)
    public String edit(@PathVariable Long id, Model model){
    	model.addAttribute("model", MODEL);
    	model.addAttribute("modelDisplay", MODEL_DISPLAY);
        model.addAttribute("item", surveyService.getSurveyById(id));
        return EDIT_VIEW;
    }

    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public String view(@PathVariable Long id, Model model){
    	model.addAttribute("model", MODEL);
    	model.addAttribute("modelDisplay", MODEL_DISPLAY);
        model.addAttribute("item", surveyService.getSurveyById(id));
        return DISPLAY_VIEW;
    }

    @RequestMapping(value = "delete/{id}", method = RequestMethod.GET)
    public String delete(@PathVariable Long id, Model model){
    	//surveyService.delete(id);
        return "redirect:/admin/" + MODEL ;
    }
       
    @RequestMapping(value = "save/section", method = RequestMethod.POST)
    public String saveSection(SurveySection surveySection, Model model){
    	model.addAttribute("model", MODEL);
    	model.addAttribute("modelDisplay", MODEL_DISPLAY);
    	SurveySection saved = surveyService.save(surveySection);
        return "redirect:/admin/" + MODEL + "/" + saved.getSurvey().getId();
    }
    
    @RequestMapping(value = "save/question", method = RequestMethod.POST)
    public String saveQuestion(SurveyQuestion surveyQuestion, Model model){
    	model.addAttribute("model", MODEL);
    	model.addAttribute("modelDisplay", MODEL_DISPLAY);
    	SurveyQuestion saved = surveyService.save(surveyQuestion);
        return "redirect:/admin/" + MODEL + "/" + saved.getSurveyId();
    }
     
}
