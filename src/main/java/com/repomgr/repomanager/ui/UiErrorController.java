package com.repomgr.repomanager.ui;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UiErrorController implements ErrorController {

    @GetMapping(value = "/error")
    public String handleError() {
        return "redirect:/public/index.html";
    }

    @Override
    public String getErrorPath() {
        return "redirect:/public/index.html";
    }
}
