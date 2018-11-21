package com.accounting.controller;

import java.io.BufferedInputStream;
import java.io.Console;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.websocket.server.PathParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationHome;
import org.springframework.context.annotation.Configuration;
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
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.w3c.dom.css.ElementCSSInlineStyle;

import com.accounting.entity.Log;
import com.accounting.entity.Report;
import com.accounting.entity.User;
import com.accounting.entity.Voucher;
import com.accounting.service.LogService;
import com.accounting.service.ReportService;
import com.accounting.service.UserService;

import groovy.util.FileNameByRegexFinder;

@Controller
public class ReportController {
	@Autowired
	private ReportService reportService;
	@Autowired
	private LogService logService;
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private String CLASSPATH = new ApplicationHome(this.getClass()).getSource().getParentFile().getPath();

	@Configuration
	public class WebMvcConfigurer extends WebMvcConfigurerAdapter {

		// 访问静态资源
		@Override
		public void addResourceHandlers(ResourceHandlerRegistry registry) {
			registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
			// SAVEPATH就是上面的basePath，当请求Url包含static/files/时，就会以文件路径来访问basePath下面的文件
			registry.addResourceHandler("/upload/**").addResourceLocations("file:" + CLASSPATH + "/upload/");
			super.addResourceHandlers(registry);
		}

	}
	
	
	// 上传日报表页面
	@GetMapping("report/toAdd")
	public String add(Model model,HttpSession session) {
		if(session.getAttribute("user")==null) {
			return "index";
		} else {
			Page<Report> reports = reportService.findRecent();
			if(!reports.getContent().isEmpty()) {
				model.addAttribute("recentReports",reports);
				logger.info(reports.getContent().get(0).getFilePath());
			}
			
			return "report_add";
		}
		
	}
	
	// 查询日报在voucher里面
	

	// 上传日报
	@PostMapping("report/add")
	public String upload(@RequestParam("file") MultipartFile file, @RequestParam("recordDate") String recordDate,
			HttpServletRequest request, HttpSession session) throws Exception, IOException {
		logger.info("try to add report");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
		User user = (User) session.getAttribute("user");
		if (user==null) {
			return "index";
		} else {
			if (!file.isEmpty()) {

				String imagePath = "/upload/csv/";
				String uploadDir = CLASSPATH+imagePath;
				uploadDir = uploadDir.replace('\\', '/');
				String filepath=file.getOriginalFilename();
				String filename="";
				logger.info(uploadDir+filename);
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
					Report report = new Report(date, imagePath+filename, user);
					reportService.save(report);
					logService.save(new Log("增加日报表"+filename,user));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			return "redirect:/report/toAdd";
		}
		
	}
	
	// 日报详情
	@GetMapping("report/report/{report_id}")
	public String report(Model model,@PathVariable("report_id") Long report_id,HttpServletResponse response,HttpSession session) {
		Report report = reportService.findById(report_id);
		String fileName=report.getFilePath();
		logger.info(fileName);
		
		File file = new File(CLASSPATH+fileName);
		Long filelength = file.length();
		byte[] filecontent = new byte [filelength.intValue()];
		try {
			FileInputStream inputStream = new FileInputStream(file);
			inputStream.read(filecontent);
			inputStream.close();
		} catch (FileNotFoundException e) {
			// TODO: handle exception
			e.printStackTrace();
		} catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		String content;
		try {
			content = new String(filecontent,"GB2312");
			logger.info(content);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			content="";
		}
			
		model.addAttribute("content",content);
		User user = (User) session.getAttribute("user");
		logService.save(new Log("查看日报表"+report.getFilePath(),user));
		return "report";
	}
	
	//删除日报
	@GetMapping("report/delete/{id}")
	public String report(@PathVariable("id") Long id,HttpSession session) {
		reportService.delete(id);
		User user = (User) session.getAttribute("user");
		if (user==null) {
			return "index";
		} else {
			logService.save(new Log("删除日报表"+id,user));
			return "redirect:/report/toAdd";
		}
		
	}
}
