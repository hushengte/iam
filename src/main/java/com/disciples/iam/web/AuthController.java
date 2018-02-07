package com.disciples.iam.web;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.disciples.iam.SecurityUtils;

@Controller
public class AuthController {
	
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public Object loginPage(String error, HttpServletRequest request) {
		if (SecurityUtils.getPrincipal() != null) {
			return new RedirectView("/index.html");
		}
		ModelAndView mv = new ModelAndView("login");
		if (StringUtils.hasText(error)) {
			mv.addObject("error", true);
		}
		return mv;
	}

}
