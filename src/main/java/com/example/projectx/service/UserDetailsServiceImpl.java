package com.example.projectx.service;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.projectx.dao.AppUserDao;
import com.example.projectx.form.UserForm;
import com.example.projectx.model.AppRole;
import com.example.projectx.model.AppUser;
import com.example.projectx.model.Journal;
import com.example.projectx.model.Qualification;
import com.example.projectx.model.Title;
import com.example.projectx.repository.UserRepository;
import com.example.projectx.utils.EncryptedPasswordUtils;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {
 
	@Value("${upload.path.profile}")
	private String profilePath;
	
	@Autowired
    private AppUserDao userDao;
    
    @Autowired
    private UserRepository userRepo;
 
    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
		com.example.projectx.model.AppUser user = userDao.getActiveUser(userName);
		
		if(user == null){
			throw new UsernameNotFoundException("Invalid username or password.");
		}
		
		System.out.println("Role="+user.getRole());
		GrantedAuthority authority = new SimpleGrantedAuthority(user.getRole());
		UserDetails userDetails = (UserDetails)new User(user.getUserName(),
				user.getPassword(), Arrays.asList(authority));
		return userDetails;		
		
	}
    
    public List<AppUser> getAllEditors()
    {
    	return userDao.getAllEditors();
    }
    
    public AppUser getUserByUsername(String userName)
    {
    	return userDao.findUserAccount(userName);
    }

	public List<AppUser> getAllAuthors() {
		
		return userDao.getAllAuthors();
	}
	
	public List<AppRole> getAllRoles()
	{
		return userDao.getAllRoles();
	}
	public List<Title> getAllTitles()
	{
		return userDao.getAllTitles();
	}
	public List<Qualification> getAllQualifications()
	{
		return userDao.getAllQualifications();
	}
	
	public String addUser(UserForm user)
	{
		String message;
		AppUser newUser = new AppUser();
		AppUser u = userDao.findUserAccount(user.getUserName());
		
		if(u==null)
		{
			newUser.setTitle(user.getTitle());
			newUser.setFullName(user.getFullName());
			newUser.setAddress1(user.getAddress1());
			newUser.setAddress2(user.getAddress2());
			
			if(user.getEmail().equals(user.getConfirmEmail()))
				newUser.setEmail(user.getEmail());
			else
				message = "ERROR: E-mail does not match";
			newUser.setPhone(user.getPhone());
			newUser.setCity(user.getCity());
			newUser.setState(user.getState());
			newUser.setQualification(user.getQualification());
			newUser.setProfession(user.getProfession());			
			
			newUser.setUserName(user.getUserName());
			if(user.getPassword().contentEquals(user.getConfirmpassword()))
				newUser.setPassword(EncryptedPasswordUtils.encrytePassword(user.getPassword()));
			else
				message = "ERROR: Password does not match";
			newUser.setRole(user.getRole());
			
			newUser.setEnabled((short) 1);
			
			MultipartFile file = user.getProfilePicture();
			
			if(file !=null)
			{
				String profileFileName = user.getUserName()+"_Profile."+FilenameUtils.getExtension(file.getOriginalFilename());				
				
				newUser.setProfilePicture(profileFileName);
				FileStorageService.uploadFile(profilePath,profileFileName, file);
				
			}
			
			userRepo.save(newUser);
			message = "User Registered successfully";
			
			
			
		}
		else
		{
			message = "ERROR: UserName "+user.getUserName()+" is already used.";
		}
		
		
		return message;	
		
		
	}
    
	public Map<String,byte[]> getAllProfilePictures()
	{
		Map<String,byte[]> profileMap = new HashMap<String,byte[]>();
		
		List<AppUser> users = userDao.getAllUsers();
		
		if(users != null)
		{
			for(AppUser j: users)
			{
				byte[] bytes = getProfilePicture(j.getUserName());
				profileMap.put(j.getUserName(), bytes);
			}
		}
		
		
		return profileMap;
	}
	
	private byte[] getProfilePicture(String username)
	{
		String fileName=getUserByUsername(username).getProfilePicture();
		
		if(fileName != null)
		{
			String absoultePath = profilePath + fileName;
			
			File f = new File(absoultePath);
			byte[] bytes=null;
			try {
				bytes =  Files.readAllBytes(f.toPath());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return bytes;
		}
		else return null;
		
	}
	
	public String getProfileLocation()
	{
		return profilePath;
		
	}
	
	public void updateProfilePicture(String uname, MultipartFile file)
	{
		AppUser user = userRepo.getOne(uname);
		
		//String fileName = "Journal_Cover_"+journal.getId()+FilenameUtils.getExtension(file.getOriginalFilename());
		if(file !=null)
		{
			String profileFileName = user.getUserName()+"_Profile."+FilenameUtils.getExtension(file.getOriginalFilename());				
			
			user.setProfilePicture(profileFileName);
			FileStorageService.uploadFile(profilePath,profileFileName, file);
			
		}
	}

	public void editUser(String uname, AppUser newUser) 
	{
		
		AppUser oldUser = userRepo.getOne(uname);
		
		copyUserFrom(newUser, oldUser);		
		
//		String oldFileName = oldUser.getProfilePicture();
//		
//		if(file !=null)
//		{
//			FileStorageService.deleteFile(profilePath,oldFileName);
//			
//			FileStorageService.uploadFile(profilePath, file);
//			
//			newUser.setProfilePicture(file.getOriginalFilename());
//			
//		}
		
		userRepo.save(oldUser);
		
		
		
	}
	private void copyUserFrom(AppUser newUser, AppUser olduser)
	{
		
		
		olduser.setTitle(newUser.getTitle());
		olduser.setFullName(newUser.getFullName());
		olduser.setAddress1(newUser.getAddress1());
		olduser.setAddress2(newUser.getAddress2());
		olduser.setPhone(newUser.getPhone());
		olduser.setCity(newUser.getCity());
		olduser.setState(newUser.getState());
		olduser.setQualification(newUser.getQualification());
		olduser.setProfession(newUser.getProfession());			
		olduser.setEmail(newUser.getEmail());	
		
		olduser.setRole(newUser.getRole());	
		
		
		
		
	}

	public void changePassword(String userName, String password) {
		
		AppUser user = userRepo.getOne(userName);
		
		String encryptedPassword = EncryptedPasswordUtils.encrytePassword(password);
		user.setPassword(encryptedPassword);
		
		userRepo.save(user);
		
	}
	
//	public void updateProfilePicture(String userid, MultipartFile file)
//	{
//		String fileName = file.getOriginalFilename();
//		
//		FileStorageService.uploadFile(coverpage, file);
//		
//		journalRepo.updateCoverPage(userid,fileName);
//	}
}
