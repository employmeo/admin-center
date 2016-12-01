package com.talytica.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {

    @RequestMapping("/admin/index")
    public String  index(Model model){
        // return withView(model, "segment/list");

    	return "fragments/segment/list";
    }
}
