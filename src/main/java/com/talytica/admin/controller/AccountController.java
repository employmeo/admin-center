package com.talytica.admin.controller;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.ws.rs.FormParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.employmeo.data.model.Account;
import com.employmeo.data.model.User;
import com.employmeo.data.service.AccountService;
import com.employmeo.data.service.UserService;
import com.stripe.exception.StripeException;
import com.stripe.model.Card;
import com.stripe.model.Customer;
import com.stripe.model.Invoice;
import com.stripe.model.Plan;
import com.stripe.model.Source;
import com.stripe.model.Subscription;
import com.talytica.common.service.BillingService;
import com.talytica.common.service.EmailService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/admin/account")
public class AccountController {
	
	@Autowired
	AccountService accountService;
	@Autowired
	UserService userService;
	@Autowired
	BillingService billingService;
	@Autowired
	EmailService emailService;
	
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
        model.addAttribute("users", accountService.getUsersForAccount(id));
    	model.addAttribute("model", MODEL);
    	model.addAttribute("modelDisplay", MODEL_DISPLAY);
    	model.addAttribute("item", account);
        return DISPLAY_VIEW;
    }
    
    @RequestMapping(value = "{id}/stripe", method = RequestMethod.GET)
    public String getStripe(@PathVariable Long id, Model model) {
    	Account account = accountService.getAccountById(id);
    	model.addAttribute("model", MODEL);
    	model.addAttribute("modelDisplay", MODEL_DISPLAY);
        model.addAttribute("item", account);
        model.addAttribute("users", accountService.getUsersForAccount(id));
    	if (null == account.getStripeId()) {
    		return STRIPE_VIEW;
    	}
		try {
			Customer customer = billingService.getCustomer(account.getStripeId());
	    	model.addAttribute("customer", customer);
        	model.addAttribute("dashlink", billingService.getDashboardPrefix(customer));
    		Subscription subscription = billingService.checkSubscription(account.getStripeId());
    		for (Subscription sub : customer.getSubscriptions().getData()) {
    			if (billingService.getActiveSubscriptionStatuses().contains(sub.getStatus())) {
    				subscription = sub;
    			}
    		}
        	if (subscription != null) {
        		model.addAttribute("subscription", subscription);
	        	model.addAttribute("upcomingInvoice",billingService.getCustomerNextInvoice(account.getStripeId()));
	        	model.addAttribute("invoices",billingService.getCustomerInvoices(account.getStripeId()));
        	}
        	log.debug("Stripe customer details:", customer);
        } catch (StripeException se) {
        	log.error("Failed to get {} details from stripe",account.getAccountName(), se);
        }
        return STRIPE_VIEW;  
    }

    @RequestMapping(value = "{id}/stripe", method = RequestMethod.POST)
    public String insertInStripe(@PathVariable Long id, @FormParam(value="userId") Long userId, Model model) throws Exception {
    	Account account = accountService.getAccountById(id);
    	User user = userService.getUserById(userId);
    	Customer customer = null;
    	if ((account.getStripeId() == null) || (account.getStripeId().isEmpty())) {
    		try {
	        	customer = billingService.createCustomerFor(account, user);
	        	account.setStripeId(customer.getId());
	        	accountService.save(account);
            } catch (StripeException se) {
            	log.error("Failed to get link this account to stripe", se);
            }    
        }
        return getStripe(id, model);
    }
    
    @RequestMapping(value = "{id}/subscribe", method = RequestMethod.POST)
    public String subscribeTo(@PathVariable Long id,
    						  @FormParam(value="planId") String planId,
    						  @FormParam(value="quantity") Integer quantity, 
    						  @FormParam(value="trialPeriod") Integer trialPeriod, 
    						  Model model) {
    	Account account = accountService.getAccountById(id);
        try {
	    	if ((account.getStripeId() != null) && (!account.getStripeId().isEmpty())) {
	    		Customer customer = billingService.getStripeCustomer(account);
	    		billingService.subscribeCustomerToPlan(account.getStripeId(), planId, quantity, trialPeriod);
	    	}
        } catch (StripeException se) {
        	log.error("Failed to get subscribe account {} to {}", id, planId, se);
        }
        
        return getStripe(id, model);
    }

    @RequestMapping(value = "{id}/previewinvoice", method = RequestMethod.POST)
    public String sendPreview(@PathVariable Long id, Model model) {
    	Account account = accountService.getAccountById(id);
    	UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	String email = user.getUsername();
		try {
			Invoice invoice = billingService.getCustomerNextInvoice(account.getStripeId());
			emailService.sendInvoice(account, invoice, email);
        } catch (StripeException se) {
        	log.error("Failed to get {} details from stripe",account.getAccountName(), se);
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
    public List<Plan> getAllPlans() {
    	try {
    		return billingService.getAllPlans();
    	} catch (Exception e) {
    		return new ArrayList<Plan>();
    	}
    }
    
    @ModelAttribute("stripePK")
    public String getStripePK(){  	
        return billingService.getStripePK();
    }
}
