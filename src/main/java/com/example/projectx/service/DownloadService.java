package com.example.projectx.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.projectx.form.AddDownloadForm;

import com.example.projectx.form.AddNoticeForm;
import com.example.projectx.model.Article;

import com.example.projectx.model.Download;
import com.example.projectx.model.Notice;
import com.example.projectx.repository.DownloadRepository;
import com.example.projectx.repository.NoticeRepository;

@Service
public class DownloadService {
	
	@Value("${upload.path.download}")
    private String downloadspath;
	
	@Value("${upload.path.article}")
    private String articlespath;
	
	@Value("${upload.path.journal}")
    private String journalspath;
	
	@Value("${upload.path.coverimage}")
    private String coverpagepath;
	
	@Value("${upload.path}")
    private String basepath;
	
		
	@Autowired
	private DownloadRepository downloadRepo;
	
	@Autowired
	private NoticeRepository noticeRepo;
	
	
	public void addDownload(AddDownloadForm download)
	{
		String title = download.getDownloadTopic();
		MultipartFile file = download.getDownloadFilePath();
		
		FileStorageService.uploadFile(downloadspath, file);
		
		// add in database
		// Download Object
		Download d = new Download();
		d.setDownloadTopic(title);
		d.setDownloadFilePath(file.getOriginalFilename());
		
        d.setUploadedDate(new java.sql.Date(System.currentTimeMillis()));
        
        
        
        downloadRepo.save(d);
	}
	
	public String getDownloadPath(String type)
	{
		if(type.equalsIgnoreCase("article"))
			return articlespath;
		else if (type.equalsIgnoreCase("journal"))
			return journalspath;
		else if	(type.equalsIgnoreCase("cover"))
			return coverpagepath;
		else if	(type.equalsIgnoreCase("download"))
			return downloadspath;
		else
			return basepath;
	}


	
	public void editDownload(int id, AddDownloadForm download)
	{
		String title = download.getDownloadTopic();
		
		String fileName = download.getDownloadFilePath().getOriginalFilename();
		System.out.println("file name is :"+fileName);
		System.out.println("file name is :"+title);
		
		//download.setdownloadTopic(title);		
        
		FileStorageService.uploadFile(downloadspath, download.getDownloadFilePath());
        
        Date date = new java.sql.Date(System.currentTimeMillis());

        downloadRepo.editDownload(id, title, fileName, date);
	}

	
	public void addNotice(AddNoticeForm notice) {
		System.out.println("reached to addnotice of download service:");
		String noticenumber  = notice.getNoticeNumber();
		String noticetitle = notice.getNoticeTitle();
		String noticetext = notice.getNoticeText();
		
		String noticefilename = notice.getNoticeFileName().getOriginalFilename();
		MultipartFile file = notice.getNoticeFileName();
		
		FileStorageService.uploadFile(downloadspath, file);
		
		// add in database
		// Download Object
		Notice n = new Notice();
		
		n.setNoticeNumber(noticenumber);
		n.setNoticeText(noticetext);
		n.setNoticeTitle(noticetitle);
		n.setNoticeFileName(noticefilename);
        n.setUploadedDate(new java.sql.Date(System.currentTimeMillis()));
        
        
        
        noticeRepo.save(n);
		
	}

	public void editnotice(int id, AddNoticeForm notice) {
		String noticenumber  = notice.getNoticeNumber();
		String noticetitle = notice.getNoticeTitle();
		String noticetext = notice.getNoticeText();
		
		String noticefilename = notice.getNoticeFileName().getOriginalFilename();
		MultipartFile file = notice.getNoticeFileName();
		
		FileStorageService.uploadFile(downloadspath, file);
		Date date = new java.sql.Date(System.currentTimeMillis());
		
		noticeRepo.updatenotice(id,noticenumber,noticetitle);
		
	}
}
