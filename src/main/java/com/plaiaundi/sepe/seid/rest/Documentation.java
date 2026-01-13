package com.plaiaundi.sepe.seid.rest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class Documentation {
    
    @GetMapping({"","/"})
    public String wellcome() {
        return "redirect:/swagger-ui/index.html";
    }
    

}
