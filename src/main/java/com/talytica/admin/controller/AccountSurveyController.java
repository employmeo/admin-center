package com.talytica.admin.controller;

import java.lang.reflect.Field;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.employmeo.data.model.Account;
import com.employmeo.data.model.AccountSurvey;
import com.employmeo.data.service.AccountService;
import com.employmeo.data.service.AccountSurveyService;
import com.google.common.collect.Sets;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/admin/accountsurvey")
public class AccountSurveyController {

	@Autowired
	AccountService accountService;
	@Autowired
	AccountSurveyService accountSurveyService;

	private static final String FRAGMENT_ROOT = "model/";
	private static final String MODEL = "accountsurvey";
	private static final String MODEL_DISPLAY = "Account Survey";
	private static final String LIST_VIEW = FRAGMENT_ROOT + MODEL + "/list";
	//private static final String CREATE_VIEW = FRAGMENT_ROOT + MODEL + "/create";
	private static final String EDIT_VIEW = FRAGMENT_ROOT + MODEL + "/edit";
	private static final String DISPLAY_VIEW = FRAGMENT_ROOT + MODEL + "/view";
	private static final Class MODEL_CLASS= AccountSurvey.class;

    @RequestMapping(value = {"","/list"}, method = RequestMethod.GET)
    public String list(Model model) {
    	model.addAttribute("model", MODEL);
    	model.addAttribute("modelDisplay", MODEL_DISPLAY);
    	Set<Account> accounts = accountService.getAllAccounts();
    	Set<AccountSurvey> surveys = Sets.newConcurrentHashSet();
    	for (Account account : accounts) surveys.addAll(account.getAccountSurveys());
    	model.addAttribute("items", surveys);
    	return LIST_VIEW;
    }
   
    @RequestMapping(value = "create", method = RequestMethod.GET)
    public String create(Model model){
    	model.addAttribute("model", MODEL);
    	model.addAttribute("modelDisplay", MODEL_DISPLAY);
    	model.addAttribute("item", new AccountSurvey());
    	return EDIT_VIEW;
    }
    
    @RequestMapping(value = "save", method = RequestMethod.POST)
    public String save(AccountSurvey survey, Model model) {
    	AccountSurvey saved = accountSurveyService.save(survey);
        return "redirect:/admin/" + MODEL + "/" + saved.getId();
    }

    @RequestMapping(value = "edit/{id}", method = RequestMethod.GET)
    public String edit(@PathVariable Long id, Model model){
    	model.addAttribute("model", MODEL);
    	model.addAttribute("modelDisplay", MODEL_DISPLAY);
        model.addAttribute("item", accountSurveyService.getAccountSurveyById(id));
        return EDIT_VIEW;
    }
    
    @RequestMapping(value = "delete/{id}", method = RequestMethod.GET)
    public String delete(@PathVariable Long id, Model model){
    	//model.addAttribute("model", MODEL);
    	//model.addAttribute("modelDisplay", MODEL_DISPLAY);
    	//accountSurveyService.delete(accountSurveyService.getAccountSurveyById(id));
        return list(model);
    }
    
    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public String view(@PathVariable Long id, Model model){
    	model.addAttribute("model", MODEL);
    	model.addAttribute("modelDisplay", MODEL_DISPLAY);
        model.addAttribute("item", accountSurveyService.getAccountSurveyById(id));
        return DISPLAY_VIEW;
    }
     
    @ModelAttribute("fieldnames")
    public Field[] getFieldNames() {  	
        return MODEL_CLASS.getDeclaredFields();
    }
}
