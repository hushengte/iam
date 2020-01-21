package com.disciples.iam.web;

import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.disciples.iam.util.SecurityUtils;

@RestController
public class LoginController {
    
    private static final String URL_INDEX = "/index.html";
    
	@RequestMapping("/login")
	public Object loginPage(String error) {
		if (SecurityUtils.getPrincipal() != null) {
			return new RedirectView(URL_INDEX);
		}
		ModelAndView mv = new ModelAndView("login");
		if (StringUtils.hasText(error)) {
			mv.addObject("error", true);
		}
		return mv;
	}

}
