package com.example.projectx.controller;

import java.security.Principal;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.projectx.form.AddDownloadForm;
import com.example.projectx.form.AddNoticeForm;
import com.example.projectx.form.UserForm;
import com.example.projectx.model.AppUser;
import com.example.projectx.model.Download;
import com.example.projectx.repository.DownloadRepository;
import com.example.projectx.repository.NoticeRepository;
import com.example.projectx.repository.UserRepository;
import com.example.projectx.service.DownloadService;
import com.example.projectx.service.UserDetailsServiceImpl;
import com.example.projectx.utils.WebUtils;

@Controller
public class AdminController {
	@Autowired
	private DownloadRepository downloadrepo;
	
	@Autowired
	private DownloadService downloadService;
	
	@Autowired
	private NoticeRepository noticerepo;
	
	@Autowired
	private UserRepository userrepo;
	
	@Autowired
	private UserDetailsServiceImpl userService;
	
	
	 
    @RequestMapping(value = "/admin", method = RequestMethod.GET)
    public String adminPage(Model model, Principal principal) {
         
        User loginedUser = (User) ((Authentication) principal).getPrincipal();
 
        
        String userInfo = WebUtils.toString(loginedUser);
        model.addAttribute("userInfo", userInfo);
        model.addAttribute("profiles", userService.getAllProfilePictures());
        model.addAttribute("currentProfile", userService.getAllProfilePictures().get(loginedUser.getUsername()));
        
       // model.addAttribute("users", userrepo.findAll()); 
        return "/admin/adminpage";
    }
	
	@RequestMapping(value = "/admin/user/add-user", method = RequestMethod.GET)
    public String addUser(Model model) {
    	model.addAttribute("user", new UserForm());
    	model.addAttribute("roles", userService.getAllRoles());
    	model.addAttribute("titles", userService.getAllTitles());
    	model.addAttribute("qualifications", userService.getAllQualifications());
    	//model.addAttribute("message", null);
        return "/admin/user/user-form";
    }
    @RequestMapping(value = "/admin/user/add-user", method = RequestMethod.POST)
    public String addUser(@ModelAttribute UserForm user,BindingResult result, Model model) {
    	
    	String returnPage;
    	
    	AppUser existing = userService.getUserByUsername(user.getUserName());
    	if (existing != null){
            result.rejectValue("email", null, "There is already an account registered with that username");
        }
    	if (result.hasErrors()){
    		model.addAttribute("user", new UserForm());
        	model.addAttribute("roles", userService.getAllRoles());
        	model.addAttribute("titles", userService.getAllTitles());
        	model.addAttribute("qualifications", userService.getAllQualifications());
        	
            return "/admin/user/user-form";
        }
    	
    	String message = userService.addUser(user);
    	if(message.startsWith("ERROR"))
    	{
    		model.addAttribute("user", new UserForm());
        	model.addAttribute("roles", userService.getAllRoles());
        	model.addAttribute("titles", userService.getAllTitles());
        	model.addAttribute("qualifications", userService.getAllQualifications());
    		
    		returnPage = "/admin/user/user-form";
    	}
    	else
    		returnPage = "/admin/user/user-list";
    	
    	model.addAttribute("message", message);
    			
    	model.addAttribute("users", userrepo.findAll());
    	
        return returnPage;
    }
    
    
	
	  @RequestMapping(value = "/admin/user", method = RequestMethod.GET) 
	  public String userPage(Model model, Principal principal) { 
		  
	  model.addAttribute("users", userrepo.findAll());
	  User loginedUser = (User) ((Authentication) principal).getPrincipal();
	  
      String userInfo = WebUtils.toString(loginedUser);
      model.addAttribute("userInfo", userInfo);
      
      model.addAttribute("profiles", userService.getAllProfilePictures());
      
	  return "/admin/user/user-list"; 
	  }
	 
    
    
    @RequestMapping(value = "/admin/user/edit-user", method = RequestMethod.GET)
    public String editUser(@RequestParam String uname, Model model) {
    	AppUser user = userrepo.findById(uname).get();
    	
		model.addAttribute("user", user);
		model.addAttribute("profiles", userService.getAllProfilePictures());
		model.addAttribute("roles", userService.getAllRoles());
    	model.addAttribute("titles", userService.getAllTitles());
    	model.addAttribute("qualifications", userService.getAllQualifications());
    	
		return "/admin/user/edit-user";
    }
    
    @RequestMapping(value = "/admin/user/edit-user", method = RequestMethod.POST)
    public String editUserPage(@RequestParam("userName") String uname, @ModelAttribute AppUser user, Model model) {

    	userService.editUser(uname, user);
    	model.addAttribute("profiles", userService.getAllProfilePictures());
        return "redirect:/admin/user";
    }
    
    @RequestMapping(value = "admin/user/delete-user", method = RequestMethod.GET)
    public String delUserPage(@RequestParam String uname,Model model) {
    	userrepo.deleteById(uname);
    	model.addAttribute("users", userrepo.findAll());
    	model.addAttribute("profiles", userService.getAllProfilePictures());
    	//System.out.println(uname);
        return "/admin/user/user-list";
    }
    
    @RequestMapping(path = "/admin/user/add-profile", method = RequestMethod.GET)
	public String add_cover(@RequestParam String uname, Model model, Principal principal) {
		
		model.addAttribute("uname", uname);
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		model.addAttribute("currentProfile", userService.getAllProfilePictures().get(loginedUser.getUsername()));
		//return "redirect:/admin/user/add-profile";
		return "/admin/user/add-profile";
	}
	
	@RequestMapping(path = "/admin/user/add-profile", method = RequestMethod.POST)
	public String addCoverPage(@RequestParam("uname") String userName, MultipartFile file, Model model, Principal principal) {
		
		System.out.println("Updated JID="+userName);
		
		userService.updateProfilePicture(userName, file);
		

	    User loginedUser = (User) ((Authentication) principal).getPrincipal();
		model.addAttribute("currentProfile", userService.getAllProfilePictures().get(loginedUser.getUsername()));
		model.addAttribute("users", userrepo.findAll());
    	model.addAttribute("profiles", userService.getAllProfilePictures());
	    
		return "/admin/user/user-list";
	}
	
	@RequestMapping(path = "/admin/user/reset-password", method = RequestMethod.GET)
	public String changePassword(@RequestParam String uname, Model model, Principal principal) {
		
		//AppUser usr = userService.getUserByUsername(uname);
		model.addAttribute("uname", uname);
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		model.addAttribute("currentProfile", userService.getAllProfilePictures().get(loginedUser.getUsername()));
		//return "redirect:/admin/user/add-profile";
		return "/admin/user/reset-password";
	}
	
	@RequestMapping(path = "/admin/user/reset-password", method = RequestMethod.POST)
	public String ChangePassowrdPost(@RequestParam String userName, @RequestParam String password, @RequestParam String confirmPassword,  Model model, Principal principal) {
		
		System.out.println("Updated User ID="+userName);
		
		String message = null;
		
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		model.addAttribute("currentProfile", userService.getAllProfilePictures().get(loginedUser.getUsername()));
		
		if(password != null && confirmPassword != null)
		{
			if(password.equals(confirmPassword))
			{
				userService.changePassword(userName, password);
				
				model.addAttribute("users", userrepo.findAll());
		    	model.addAttribute("profiles", userService.getAllProfilePictures());
		    	
		    	model.addAttribute("message", message);
			    
				return "/admin/user/user-list";
			}
			else
			{
				message = "Passwords do not match !!";
				model.addAttribute("message", message);
				return "/admin/user/reset-password";
			}
				
		}
		else
		{
			message = "Passowrd can not be empty";
			model.addAttribute("message", message);
			return "/admin/user/reset-password";
		}	   
		
	}
	
	 @RequestMapping(value = "/admin/downloads", method = RequestMethod.GET)
	    public String getDownloads(Model model) {
	    	model.addAttribute("downloads",downloadrepo.findAll() );
	        return "admin/downloads/download-list";
	    }
	    
	 @RequestMapping(value = "/admin/downloads/add-download", method = RequestMethod.GET)
	    public String addDownload(Model model) {
	    	model.addAttribute("download", new AddDownloadForm());
	        return "admin/downloads/add-download";
	    }
	    @RequestMapping(value = "/admin/downloads/add-download", method = RequestMethod.POST)
	    public String postDownload(@ModelAttribute AddDownloadForm download ,Model model) {
	    	
	    	downloadService.addDownload(download);
	    	model.addAttribute("users", downloadrepo.findAll());
	        return "admin/downloads/download-list";
	    }
	    
	    
	    
	    
	    @RequestMapping(value = "/admin/notices/add-notice", method = RequestMethod.GET)
	    public String addNotice(Model model) {
	    	model.addAttribute("notice", new AddNoticeForm());
	        return "admin/notices/add-notice";
	    }
	    @RequestMapping(value = "/admin/downloads/add-notice", method = RequestMethod.POST)
	    public String postNotice(@ModelAttribute AddNoticeForm notice ,Model model) {
	    	//download service provides downloads and notice related services,hence downloadservice is used here
	    	downloadService.addNotice(notice);
	    	model.addAttribute("notices", noticerepo.findAll());
	        return "admin/downloads/notice-list";
	    }
	    
}
