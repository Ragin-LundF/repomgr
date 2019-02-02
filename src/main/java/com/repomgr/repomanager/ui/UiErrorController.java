package com.repomgr.repomanager.ui;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class UiErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError() {
        return "redirect:/public/index.html";
    }

    @Override
    public String getErrorPath() {
        return "redirect:/public/index.html";
    }
}
