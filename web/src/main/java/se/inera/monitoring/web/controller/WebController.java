package se.inera.monitoring.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class WebController {

    @RequestMapping("/")
    public String getIndexPage() {
        return "index";
    }
    
}
