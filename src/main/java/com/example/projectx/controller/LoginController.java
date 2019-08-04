package com.example.projectx.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.example.projectx.service.UserDetailsServiceImpl;
import com.example.projectx.utils.WebUtils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.security.Principal;

import javax.servlet.http.HttpSession;

//@SessionAttributes({"currentUser"})
@Controller
public class LoginController {
	@Autowired
	private UserDetailsServiceImpl userInfoService;
	
    private static final Logger log = LogManager.getLogger(LoginController.class);
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login() {
        return "login";
    }
    @RequestMapping(value = "/loginFailed", method = RequestMethod.GET)
    public String loginError(Model model) {
        log.info("Login attempt failed");
        model.addAttribute("error", "true");
        return "login";
    }
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout(SessionStatus session) {
        SecurityContextHolder.getContext().setAuthentication(null);
        session.setComplete();
        return "redirect:/index";
    }
    @RequestMapping(value = "/postLogin", method = RequestMethod.GET)
    public String postLogin(Model model) {
        log.info("postLogin()");
        
       // UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        Authentication loggedInUser = SecurityContextHolder.getContext().getAuthentication();
        String username = loggedInUser.getName();
        
        System.out.println("LoggedIn User: "+username);
        
        
        
        model.addAttribute("userInfo", userInfoService.loadUserByUsername(username));   
        
       
        return "redirect:/welcome";
    }

  }
    

