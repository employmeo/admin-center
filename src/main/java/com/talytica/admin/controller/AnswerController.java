package com.talytica.admin.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.employmeo.data.model.Corefactor;
import com.employmeo.data.model.Answer;
import com.employmeo.data.service.CorefactorService;
import com.employmeo.data.service.QuestionService;

@Controller
@RequestMapping("/admin/answer")
public class AnswerController {

	@Autowired
	QuestionService questionService;
	
	@Autowired
	CorefactorService corefactorService;
	


	private static final String FRAGMENT_ROOT = "model/";
	private static final String MODEL = "answer";
	private static final String MODEL_DISPLAY = "Answer";
	private static final String LIST_VIEW = FRAGMENT_ROOT + MODEL + "/list";
	private static final String CREATE_VIEW = FRAGMENT_ROOT + MODEL + "/create";
	private static final String EDIT_VIEW = FRAGMENT_ROOT + MODEL + "/edit";
	private static final String DISPLAY_VIEW = FRAGMENT_ROOT + MODEL + "/view";

    @RequestMapping(value = {"","list"}, method = RequestMethod.GET)
    public String  list(Model model) {
    	model.addAttribute("model", MODEL);
    	model.addAttribute("modelDisplay", MODEL_DISPLAY);
    	model.addAttribute("items", questionService.getAllAnswers());
    	return LIST_VIEW;
    }

    @RequestMapping(value = {"{id}/list","list"}, method = RequestMethod.GET)
    public String  listbyQuestionId(@PathVariable Long id, Model model) {
    	model.addAttribute("model", MODEL);
    	model.addAttribute("modelDisplay", MODEL_DISPLAY);
    	model.addAttribute("items", questionService.getQuestionById(id).getAnswers());
    	return LIST_VIEW;
    }
    
    @RequestMapping(value = "create", method = RequestMethod.GET)
    public String create(Model model){
    	model.addAttribute("model", MODEL);
    	model.addAttribute("modelDisplay", MODEL_DISPLAY);
    	model.addAttribute("item", new Answer());
    	return EDIT_VIEW;
    }

    @RequestMapping(value = "{id}/create", method = RequestMethod.GET)
    public String add(@PathVariable Long id, Model model){
    	model.addAttribute("model", MODEL);
    	model.addAttribute("modelDisplay", MODEL_DISPLAY);
    	model.addAttribute("question", questionService.getQuestionById(id));
    	Answer answer = new Answer();
    	answer.setQuestionId(id);
    	model.addAttribute("item", answer);
    	return EDIT_VIEW;
    }
    
    @RequestMapping(value = "save", method = RequestMethod.POST)
    public String save(Answer answer, Model model) {
    	Answer saved = questionService.save(answer);
        return "redirect:/admin/" + MODEL + "/" + saved.getId();
    }

    @RequestMapping(value = "edit/{id}", method = RequestMethod.GET)
    public String edit(@PathVariable Long id, Model model){
    	model.addAttribute("model", MODEL);
    	model.addAttribute("modelDisplay", MODEL_DISPLAY);
        model.addAttribute("item", questionService.getAnswerById(id));
        return EDIT_VIEW;
    }
    
    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public String view(@PathVariable Long id, Model model){
    	model.addAttribute("model", MODEL);
    	model.addAttribute("modelDisplay", MODEL_DISPLAY);
        model.addAttribute("item", questionService.getAnswerById(id));
        return DISPLAY_VIEW;
    }

    @RequestMapping(value = "delete/{id}", method = RequestMethod.GET)
    public String delete(@PathVariable Long id, Model model){
    	// questionService.deleteAnswer(id);
        return "redirect:/admin/" + MODEL ;
    }
    
    @ModelAttribute("allCorefactors")
    public Iterable<Corefactor> getCorefactors() {
        return corefactorService.getAllCorefactors();
    }
    

}
