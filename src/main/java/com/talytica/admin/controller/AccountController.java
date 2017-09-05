package com.talytica.admin.controller;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.FormParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.employmeo.data.model.Account;
import com.employmeo.data.service.AccountService;
import com.stripe.exception.StripeException;
import com.stripe.model.Card;
import com.stripe.model.Customer;
import com.stripe.model.Plan;
import com.stripe.model.Source;
import com.talytica.common.service.BillingService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/admin/account")
public class AccountController {

	@Autowired
	AccountService accountService;
	@Autowired
	BillingService billingService;
	
	private static final String FRAGMENT_ROOT = "model/";
	private static final String MODEL = "account";
	private static final String MODEL_DISPLAY = "account";
	private static final String LIST_VIEW = FRAGMENT_ROOT + MODEL + "/list";
	//private static final String CREATE_VIEW = FRAGMENT_ROOT + MODEL + "/create";
	private static final String EDIT_VIEW = FRAGMENT_ROOT + MODEL + "/edit";
	private static final String DISPLAY_VIEW = FRAGMENT_ROOT + MODEL + "/view";
	private static final String STRIPE_VIEW = FRAGMENT_ROOT + MODEL + "/stripe";
	private static final Class MODEL_CLASS= Account.class;

    @RequestMapping(value = {"","/list"}, method = RequestMethod.GET)
    public String  list(Model model) {
    	model.addAttribute("model", MODEL);
    	model.addAttribute("modelDisplay", MODEL_DISPLAY);
    	model.addAttribute("items", accountService.getAllAccounts());
    	return LIST_VIEW;
    }
   
    @RequestMapping(value = "create", method = RequestMethod.GET)
    public String create(Model model){
    	model.addAttribute("model", MODEL);
    	model.addAttribute("modelDisplay", MODEL_DISPLAY);
    	model.addAttribute("item", new Account());
    	return EDIT_VIEW;
    }
    
    @RequestMapping(value = "save", method = RequestMethod.POST)
    public String save(Account account, Model model) {
    	Account saved = accountService.save(account);
        return "redirect:/admin/" + MODEL + "/" + saved.getId();
    }

    @RequestMapping(value = "edit/{id}", method = RequestMethod.GET)
    public String edit(@PathVariable Long id, Model model){
    	model.addAttribute("model", MODEL);
    	model.addAttribute("modelDisplay", MODEL_DISPLAY);
        model.addAttribute("item", accountService.getAccountById(id));
        return EDIT_VIEW;
    }
    
    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public String view(@PathVariable Long id, Model model) {
    	Account account = accountService.getAccountById(id);
        try {
	    	if ((account.getStripeId() != null) && (!account.getStripeId().isEmpty())) {
	    		Customer customer = billingService.getCustomer(account.getStripeId());
	    		model.addAttribute("customer", customer);	
	    	}
        } catch (StripeException se) {
        	log.error("Failed to get Stripe data this account", se);
        }
        model.addAttribute("users", accountService.getUsersForAccount(id));
    	model.addAttribute("model", MODEL);
    	model.addAttribute("modelDisplay", MODEL_DISPLAY);
    	model.addAttribute("item", account);
        return DISPLAY_VIEW;
    }
    
    @RequestMapping(value = "{id}/stripe", method = RequestMethod.POST)
    public String insertInStripe(@PathVariable Long id, Model model) throws Exception {
    	Account account = accountService.getAccountById(id);
    	Customer customer = null;
		try {
	    	if ((account.getStripeId() == null) || (account.getStripeId().isEmpty())) {
		        	customer = billingService.createCustomerFor(account);
		        	account.setStripeId(customer.getId());
		        	accountService.save(account);
	        } else {
	        	customer = billingService.getCustomer(account.getStripeId());
	        	model.addAttribute("plans", billingService.getCustomerPlans(account.getStripeId()));
	        }
	    	model.addAttribute("customer", customer);
        } catch (StripeException se) {
        	log.error("Failed to get link this account to stripe", se);
        }    
    	model.addAttribute("model", MODEL);
    	model.addAttribute("modelDisplay", MODEL_DISPLAY);
        model.addAttribute("item", account);
        model.addAttribute("users", accountService.getUsersForAccount(id));
        return STRIPE_VIEW;  
    }
    
    @RequestMapping(value = "{id}/stripe", method = RequestMethod.GET)
    public String getStripe(@PathVariable Long id, Model model) {
    	Account account = accountService.getAccountById(id);
		try {
			Customer customer = billingService.getCustomer(account.getStripeId());
	    	model.addAttribute("customer", customer);
        	model.addAttribute("plans", billingService.getCustomerPlans(account.getStripeId()));
        	log.debug("Stripe customer details:", customer);
        } catch (StripeException se) {
        	log.error("Failed to get {} details from stripe",account.getAccountName(), se);
        }
    	model.addAttribute("model", MODEL);
    	model.addAttribute("modelDisplay", MODEL_DISPLAY);
        model.addAttribute("item", account);
        model.addAttribute("users", accountService.getUsersForAccount(id));
        return STRIPE_VIEW;  
    }

    @RequestMapping(value = "{id}/subscribe", method = RequestMethod.POST)
    public String subscribeTo(@PathVariable Long id, @FormParam(value="planId") String planId, Model model) {
    	Account account = accountService.getAccountById(id);
        try {
	    	if ((account.getStripeId() != null) && (!account.getStripeId().isEmpty())) {
	    		Customer customer = billingService.getStripeCustomer(account);
	    		billingService.subscribeCustomerToPlan(customer, planId);
	    		model.addAttribute("plans", billingService.getCustomerPlans(account.getStripeId()));
	    	}
        } catch (StripeException se) {
        	log.error("Failed to get subscribe account {} to {}", id, planId, se);
        }
        
        return getStripe(id, model);
    }

    @RequestMapping(value = "{id}/addcard", method = RequestMethod.POST) 
    public String addPaymentInfo (@PathVariable Long id,
    		@FormParam(value="stripeToken") String stripeToken,
    		@FormParam(value="stripeToken") String stripeEmail, Model model) {
    	Account account = accountService.getAccountById(id);
    	try {
    		Card card = billingService.addCardToCustomer(stripeToken, account);
        	model.addAttribute("card",card);
        } catch (StripeException se) {
        	log.error("Failed to get add payment method to: {}", account.getAccountName(), se);
        }           
    	return getStripe(id, model);
    }
    
    @ModelAttribute("fieldnames")
    public Field[] getSurveySections() {  	
        return MODEL_CLASS.getDeclaredFields();
    }

    @ModelAttribute("allplans")
    public List<Plan> getAllPlans() throws Exception {  	
        return billingService.getAllPlans();
    }
    
    @ModelAttribute("stripePK")
    public String getStripePK(){  	
        return billingService.getStripePK();
    }
}
