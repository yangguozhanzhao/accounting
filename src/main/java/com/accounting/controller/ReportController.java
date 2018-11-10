package com.accounting.controller;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.css.ElementCSSInlineStyle;

import com.accounting.entity.Report;
import com.accounting.entity.User;
import com.accounting.service.ReportService;
import com.accounting.service.UserService;

import groovy.util.FileNameByRegexFinder;

@Controller
public class ReportController {
	@Autowired
	private ReportService reportService;
    private Logger logger = LoggerFactory.getLogger(getClass());



	// 上传日报表页面
	@GetMapping("report/toAdd")
	public String scan() {
		return "report_add";
	}

	// 上传日报
	@PostMapping("report/add")
	public String upload(@RequestParam("file") MultipartFile[] file,
			@RequestParam("recordDate") String recordDate,HttpServletRequest request) {
		logger.info("try to add report");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
		if(file!=null) {
			Date date = sdf.parse(recordDate);
			String uploadDir = request.getServletContext().getRealPath("/upload/csv/");
			logger.info("path="+uploadDir);
			File dir = new File(uploadDir);
			if(!dir.exists()) {
				dir.mkdir();
			}
			for(int i;i<file.length;i++) {
				if(file[i]!=null) {
					String filename =recordDate+file[i].getOriginalFilename();
					File serverFile = new File(uploadDir+filename);
					file[i].transferTo(serverFile);
					//Report report = new Report(date,uploadDir+filename);
					//reportService.save(Report);
				}
			}
					
		}
		
		return "redirect:/report/add";
	}
	
	// 获取当天上传的日报表
	
	
	
}
