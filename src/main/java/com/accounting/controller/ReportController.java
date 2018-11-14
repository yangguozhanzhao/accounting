package com.accounting.controller;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.websocket.server.PathParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.css.ElementCSSInlineStyle;

import com.accounting.entity.Report;
import com.accounting.entity.User;
import com.accounting.entity.Voucher;
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
	public String add(Model model) {
		Page<Report> reports = reportService.findRecent();
		if(!reports.getContent().isEmpty()) {
			model.addAttribute("recentReports",reports);
			logger.info(reports.getContent().get(0).getFilePath());
		}
		
		return "report_add";
	}

	// 上传日报
	@PostMapping("report/add")
	public String upload(@RequestParam("file") MultipartFile file, @RequestParam("recordDate") String recordDate,
			HttpServletRequest request, HttpSession session) throws Exception, IOException {
		logger.info("try to add report");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
		User user = (User) session.getAttribute("user");
		if (file != null) {

			String uploadDir = request.getServletContext().getRealPath("/upload/csv/");
			logger.info("path=" + uploadDir);
			String filepath=file.getOriginalFilename();
			String filename="";
			// IE8是完整路径
			if(filepath.lastIndexOf("\\")>0) {
				filename = recordDate +'_'+ filepath.substring(filepath.lastIndexOf("\\")+1);
			}else {
				filename=recordDate+'_'+filepath;
			}
			logger.info(filename);
			File fileDir = new File(uploadDir,filename);
			if (!fileDir.getParentFile().exists()) {
				fileDir.getParentFile().mkdirs();
			}
			

			
			
			file.transferTo(new File(uploadDir+filename));
			try {
				Date date = sdf.parse(recordDate);
				Report report = new Report(date, uploadDir+filename, user);
				reportService.save(report);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return "redirect:/report/toAdd";
	}
	
	// 日报详情
	@GetMapping("report/report/{report_id}")
	public String report(Model model,@PathVariable("report_id") Long report_id) {
		Report report = reportService.findById(report_id);
		model.addAttribute("report",report);
		return "report";
	}
	
	//删除日报
	@GetMapping("report/delete/{id}")
	public String report(@PathVariable("id") Long id) {
		reportService.delete(id);
		return "redirect:/report/toAdd";
	}
}
