package com.disciples.iam.web;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.web.WebAttributes;
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
	public Object loginPage() {
		if (SecurityUtils.getPrincipal() != null) {
			return new RedirectView("/index.html");
		}
		return new ModelAndView("login");
	}

	@RequestMapping(value = "/authfailed", method = RequestMethod.POST)
	public Object authfailed(HttpServletRequest request) {
		Object ex = request.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
		if (ex != null) {
			ModelAndView mv = new ModelAndView("login");
			String username = request.getParameter("username");
			if (StringUtils.hasText(username)) {
				mv.addObject("username", username);
			}
			return mv.addObject("error", Boolean.TRUE);
		}
		return new RedirectView(SecurityUtils.getPrincipal() != null ? "/index.html" : "/login.html");
	}
	
}
