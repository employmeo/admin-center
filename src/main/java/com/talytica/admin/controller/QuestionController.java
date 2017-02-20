package com.talytica.admin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.employmeo.data.model.Corefactor;
import com.employmeo.data.model.Question;
import com.employmeo.data.model.QuestionType;
import com.employmeo.data.model.SurveySection;
import com.employmeo.data.repository.QuestionTypeRepository;
import com.employmeo.data.service.CorefactorService;
import com.employmeo.data.service.QuestionService;
import com.employmeo.data.service.SurveyService;

@Controller
@RequestMapping("/admin/question")
public class QuestionController {

	@Autowired
	QuestionService questionService;
	
	@Autowired
	CorefactorService corefactorService;
	
	@Autowired
	SurveyService surveyService;
	
	@Autowired
	QuestionTypeRepository questionTypeRepository;

	private static final String FRAGMENT_ROOT = "model/";
	private static final String MODEL = "question";
	private static final String MODEL_DISPLAY = "Question";
	private static final String LIST_VIEW = FRAGMENT_ROOT + MODEL + "/list";
	//private static final String CREATE_VIEW = FRAGMENT_ROOT + MODEL + "/create";
	private static final String EDIT_VIEW = FRAGMENT_ROOT + MODEL + "/edit";
	private static final String DISPLAY_VIEW = FRAGMENT_ROOT + MODEL + "/view";

    @RequestMapping(value = {"","list"}, method = RequestMethod.GET)
    public String  list(Model model) {
    	model.addAttribute("model", MODEL);
    	model.addAttribute("modelDisplay", MODEL_DISPLAY);
    	model.addAttribute("items", questionService.getAllQuestions());
    	return LIST_VIEW;
    }

    @RequestMapping(value = "create", method = RequestMethod.GET)
    public String create(Model model){
    	model.addAttribute("model", MODEL);
    	model.addAttribute("modelDisplay", MODEL_DISPLAY);
    	model.addAttribute("item", new Question());
    	return EDIT_VIEW;
    }

    @RequestMapping(value = "save", method = RequestMethod.POST)
    public String save(Question question, Model model) {
    	Question saved = questionService.save(question);
        return "redirect:/admin/" + MODEL + "/" + saved.getQuestionId();
    }

    @RequestMapping(value = "edit/{id}", method = RequestMethod.GET)
    public String edit(@PathVariable Long id, Model model){
    	model.addAttribute("model", MODEL);
    	model.addAttribute("modelDisplay", MODEL_DISPLAY);
        model.addAttribute("item", questionService.getQuestionById(id));
        return EDIT_VIEW;
    }
    
    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public String view(@PathVariable Long id, Model model){
    	model.addAttribute("model", MODEL);
    	model.addAttribute("modelDisplay", MODEL_DISPLAY);
        model.addAttribute("item", questionService.getQuestionById(id));
        return DISPLAY_VIEW;
    }

    @RequestMapping(value = "delete/{id}", method = RequestMethod.GET)
    public String delete(@PathVariable Long id, Model model){
    	questionService.deleteQuestion(id);
        return "redirect:/admin/" + MODEL ;
    }
    
    @ModelAttribute("allCorefactors")
    public Iterable<Corefactor> getCorefactors() {
        return corefactorService.getAllCorefactors();
    }
    
    @ModelAttribute("allTypes")
    public Iterable<QuestionType> populateTypes() {
        return questionTypeRepository.findAll();
    }
    
    @ModelAttribute("allSurveySections")
    public Iterable<SurveySection> getSurveySections() {
        return surveyService.getAllSurveySections();
    }
}
