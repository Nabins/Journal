package com.example.projectx.controller;

import java.security.Principal;

import javax.mail.MessagingException;

import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.example.projectx.dto.UserDto;
import com.example.projectx.form.ArticleUploadForm;
import com.example.projectx.mail.EmailService;
import com.example.projectx.mail.Mail;
import com.example.projectx.model.AppUser;
import com.example.projectx.repository.DownloadRepository;
import com.example.projectx.repository.NoticeRepository;
import com.example.projectx.repository.PageRepository;
import com.example.projectx.repository.UserRepository;
import com.example.projectx.service.UserDetailsServiceImpl;
import com.example.projectx.utils.WebUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;

@Controller
public class IndexController {
	

	@Autowired
	private PageRepository pagerepo;
	@Autowired
	private DownloadRepository downloadrepo;
	@Autowired
	private NoticeRepository noticerepo;

	@Autowired
	private UserRepository userrepo;

	
	@Autowired
	private UserDetailsServiceImpl userService;
	
	@Autowired
    private EmailService emailService;

	
	@RequestMapping(path = "register", method = RequestMethod.GET)
	public String register(Model model) {
		return "registration";
	}
	
	@RequestMapping(path = "/resetpassword", method = RequestMethod.GET)
	public String resetPassword(Model model) {		

		return "resetpassword";
	}
	
	@RequestMapping(path = "/resetpassword", method = RequestMethod.POST)
	public String resetPassowrdPost(@RequestParam String email,  Model model) {
		
		
		String message = null;

		if(email != null)
		{
			UserDto user = userService.getUserByEmail(email);
			
			if(user != null)
			{
				Mail m = new Mail();
				
				m.setSubject("Password Reset");
				m.setTo(user.getEmail());
				m.setContent("Your login credentials have been sent as per your request. UserName: "+user.getUserName()+"; Password: "+user.getPassword());
				try {
					emailService.sendSimpleMessage(m);
				} catch (MessagingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else
			{
				message = "Your email is not found. Please enter valid email address";
				
			}
				
		}
		else
		{
			message = "Please enter valid email address";
			
		}
		model.addAttribute("message", message);

		return "/resetpassword";
   
		
	}
		
	@RequestMapping(value = { "/"}, method = RequestMethod.GET)
    public String indexPage(Model model) {
		ArticleUploadForm articleUploadForm = new ArticleUploadForm();
	    model.addAttribute("articleUploadForm", articleUploadForm);
	    
        model.addAttribute("title", "Welcome");
        model.addAttribute("page", pagerepo.getOne(1));
        model.addAttribute("downloads",downloadrepo.findAll() );
        
        model.addAttribute("notices",noticerepo.findAll() );
        model.addAttribute("users",userrepo.findAll());
        model.addAttribute("message", "This is welcome page!");
        return "index";
    }
	
	@RequestMapping(value = { "/index"}, method = RequestMethod.GET)
    public String indexPage1(Model model) {
		ArticleUploadForm articleUploadForm = new ArticleUploadForm();
	    model.addAttribute("articleUploadForm", articleUploadForm);
        model.addAttribute("title", "Welcome");
        model.addAttribute("message", "This is home page!");
        return "index";
    }
	
	@RequestMapping(value = { "/welcome" }, method = RequestMethod.GET)
    public String welcomePage(Model model) {
        model.addAttribute("title", "Welcome");
        model.addAttribute("message", "This is welcome page!");
        return "welcome";
    }
	

    
 
    @RequestMapping(value="403", method = RequestMethod.GET)
	public ModelAndView error() {
	    ModelAndView mav = new ModelAndView();
	    String errorMessage= "You are not authorized for the requested data.";
	    mav.addObject("errorMsg", errorMessage);
	    mav.setViewName("403");
	    return mav;
        }
    
    
    @RequestMapping(value = { "contact" }, method = RequestMethod.GET)
    public String contactPage(Model model) {
    	ArticleUploadForm articleUploadForm = new ArticleUploadForm();
	    model.addAttribute("articleUploadForm", articleUploadForm);
	    
    	System.out.println("hello"+ "world");
        return "contactpage/contact-page-index";
    }
    
    
    
    @RequestMapping(value = "/guidelines", method = RequestMethod.GET)
    public String authorGuideline(Model model) {
    	model.addAttribute("guidelines", pagerepo.getOne(2));
    	ArticleUploadForm articleUploadForm = new ArticleUploadForm();
	    model.addAttribute("articleUploadForm", articleUploadForm);
        return "/author-guidelines";
    }
    
    @RequestMapping(value = "/aboutus", method = RequestMethod.GET)
    public String aboutUs(Model model) {
    	model.addAttribute("aboutus", pagerepo.getOne(1));
    	ArticleUploadForm articleUploadForm = new ArticleUploadForm();
	    model.addAttribute("articleUploadForm", articleUploadForm);
        return "aboutus";
    }
    
   
    @RequestMapping(value = "/downloads", method = RequestMethod.GET)
    public String getDownloads(Model model) {
    	model.addAttribute("downloads",downloadrepo.findAll() );
    	ArticleUploadForm articleUploadForm = new ArticleUploadForm();
	    model.addAttribute("articleUploadForm", articleUploadForm);
        return "/downloads";
    }
    
    @RequestMapping(value = "/notices", method = RequestMethod.GET)
    public String getNotices(Model model) {
    	model.addAttribute("notices",noticerepo.findAll());
    	ArticleUploadForm articleUploadForm = new ArticleUploadForm();
	    model.addAttribute("articleUploadForm", articleUploadForm);
        return "notice";
    }
   
    
    
}
