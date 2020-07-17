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

    /**
     * The return value from this method is not used; the property `server.error.path`
     * must be set to override the default error page path.
     * @return the error path
     * @deprecated since 2.3.0 in favor of setting the property `server.error.path`
     */
    @Override
    @Deprecated
    public String getErrorPath() {
        return null;
    }
}
