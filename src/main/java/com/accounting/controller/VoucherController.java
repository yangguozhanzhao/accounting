package com.accounting.controller;

import java.io.BufferedOutputStream;
import java.io.Console;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.Buffer;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.aspectj.weaver.ast.Var;
import org.codehaus.groovy.ast.stmt.LoopingStatement;
import org.hibernate.annotations.Parameter;
import org.hibernate.boot.archive.scan.spi.ScanEnvironment;
import org.hibernate.validator.internal.util.privilegedactions.GetClassLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationHome;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.w3c.dom.css.ElementCSSInlineStyle;

import com.accounting.entity.Log;
import com.accounting.entity.Report;
import com.accounting.entity.User;
import com.accounting.entity.Voucher;
import com.accounting.service.LogService;
import com.accounting.service.ReportService;
import com.accounting.service.VoucherService;

import ch.qos.logback.core.joran.conditional.ElseAction;

@Controller
public class VoucherController {
	@Autowired
	private VoucherService voucherService;
	
	@Autowired
	private LogService logService;
	
	@Autowired
	private ReportService reportService;
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

	// 扫描凭证页面
	@GetMapping("voucher/toAdd")
	public String scan(HttpSession session) {
		User user = (User) session.getAttribute("user");
		if (user==null) {
			return "index";
		} else {
			return "voucher_add";
		}
		
	}

	// 添加凭证
	@PostMapping("voucher/add")
	@ResponseBody
	public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file,
			@RequestParam(value = "content", defaultValue = "无") String content,
			@RequestParam("recordDate") String recordDate, HttpSession session) {
		logger.info("try to add voucher");
		if (!file.isEmpty() && session.getAttribute("user")!= null) {
			try {
				// 上传文件路径,按照系统日期分文件夹存储
				String imagePath = "/upload/images/";
				String uploadDir = CLASSPATH + imagePath;
				uploadDir = uploadDir.replace('\\', '/');
				logger.info("path = " + uploadDir);
				// 上传文件名
				String filepath = file.getOriginalFilename();
				// 判断路径是否存在，如果不存在就创建一个
				String filename = "";
				// IE8是完整路径
				if (filepath.lastIndexOf("\\") > 0) {
					filename = filepath.substring(filepath.lastIndexOf("\\") + 1);
				} else {
					filename = filepath;
				}
				File fileDir = new File(uploadDir, filename);
				if (!fileDir.getParentFile().exists()) {
					fileDir.getParentFile().mkdirs();
				}
				// 将上传文件保存到一个目标文件当中
				file.transferTo(new File(uploadDir + filename));
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
				try {
					Date date = sdf.parse(recordDate);
					User user = (User) session.getAttribute("user");
					Voucher voucher = new Voucher(date, imagePath + filename, content, user);
					voucherService.save(voucher);
					logService.save(new Log("扫描上传凭证"+filename,user));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					logger.error("凭证保存失败");
					e.printStackTrace();
				}

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				logger.error("无文件");
			} catch (IOException e) {
				e.printStackTrace();
				// TODO: handle exception
				logger.error("图片保存失败");
			}

		} else {
			logger.warn("没有图片");
		}
		return ResponseEntity.ok().header("Content-Type", "text/plain").body("ok");
	}

	// ajax查询凭证页面前10,暂时未用
	@RequestMapping(value = "voucher/recent", method = RequestMethod.GET)
	@ResponseBody
	public Page<Voucher> showRecentVouchers() {
		Page<Voucher> voucherList = voucherService.findRecent();
		return voucherList;
	}

	// 查询凭证页面前10
	@RequestMapping(value = "toQuery", method = RequestMethod.GET)
	public String showRecentVouchers(Model model,HttpSession session) {
		User user = (User) session.getAttribute("user");
		
		if (user == null) {
			return "index";
		} else {
			Page<Voucher> voucherList = voucherService.findRecent();
			model.addAttribute("recentVouchers", voucherList);
			logger.info("v="+voucherList.getContent());
			return "query";
		}
	}
	
	// 查询日报或者凭证
	@RequestMapping(value = "query")
	public String query(Model model,HttpServletRequest request,HttpSession session) throws ParseException {
		User user = (User) session.getAttribute("user");
		if (user == null) {
			return "index";
		} else {
			String date1=request.getParameter("recordDate1");
			String date2=request.getParameter("recordDate2");
			String key=request.getParameter("key");
			String radio=request.getParameter("optionsRadios");
			String pageNum= request.getParameter("pageNum");
			int pageSize = 20;
			logger.info(date1+date2+key+radio+pageNum);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
			Date startDate = sdf.parse(date1);
			Date endDate =sdf.parse(date2);
			
			//查询凭证
			if (radio.equals("report")) {
				logger.info("searcher report");
				logger.info("pa="+startDate+endDate+key+radio+pageNum);
				Page<Report> rPage = reportService.findByDateR(Integer.parseInt(pageNum), pageSize, startDate, endDate);
				logger.info("查询完毕");
				if(rPage!=null) {
					logger.info("结果为report="+rPage.getContent());
					model.addAttribute("rPage", rPage);
					model.addAttribute("startDate",date1);
					model.addAttribute("endDate",date2);
					model.addAttribute("content",key);
					model.addAttribute("radio",radio);
				} else {
					logger.info("report 结果为空");
				}
			} else if(radio.equals("voucher")) {
				
				Page<Voucher> vPage=voucherService.findByDateAndContent(Integer.parseInt(pageNum), pageSize, startDate, endDate, key);
				//
				if(vPage!=null) {
					logger.info("结果为voucher="+vPage.getContent());
					model.addAttribute("vPage", vPage);
					model.addAttribute("startDate",date1);
					model.addAttribute("endDate",date2);
					model.addAttribute("content",key);
					model.addAttribute("radio",radio);
				} else {
					logger.info("voucher结果为空");
				}
			}
			return "query";
		}
		
	} 

	// 根据id显示详情
	@GetMapping("voucher/{id}")
	public String voucher(Model model, @PathVariable("id") Long id,HttpSession session) {
		User user = (User) session.getAttribute("user");
		if (user==null) {
			return "index";
		} else {
			Voucher voucher = voucherService.findById(id);
			model.addAttribute("voucher", voucher);
			logService.save(new Log("查看凭证"+id,user));
			return "voucher";
		}
	}
	
	// 根据id显示详情
	@GetMapping("voucher/prev/{id}")
	public String prev(Model model, @PathVariable("id") Long id,HttpSession session) {
		User user = (User) session.getAttribute("user");
		if (user == null) {
			return "index";
		} else {
			// 只能搜到10以内
			int n = 10;
			id=id-1;
			while(!voucherService.existsById(id) && n>0) {
				id=id-1;
				n=n-1;
			}
			Voucher voucher = voucherService.findById(id);
			model.addAttribute("voucher", voucher);
			logService.save(new Log("查看凭证"+id,user));
			return "redirect:/voucher/"+id;
		}
		
	}
	
	// 根据id显示详情
		@GetMapping("voucher/next/{id}")
		public String next(Model model, @PathVariable("id") Long id,HttpSession session) {
			User user = (User) session.getAttribute("user");
			if (user == null) {
				return "index";
			} else {
				// 只能搜到10以内
				int n = 10;
				id=id+1;
				while(!voucherService.existsById(id) && n>0) {
					id=id+1;
					n=n-1;
				}
				Voucher voucher = voucherService.findById(id);
				model.addAttribute("voucher", voucher);
				logService.save(new Log("查看凭证"+id,user));
				return "redirect:/voucher/"+id;
			}
			
		}

	// 删除
	@RequestMapping("voucher/delete/{id}")
	public String report(@PathVariable("id") Long id,HttpSession session){
		User user = (User) session.getAttribute("user");
		if (user==null) {
			return "index";
		} else {
			voucherService.delete(id);
			logService.save(new Log("删除凭证"+id,user));
			return "redirect:/toQuery";
		}
		
	}

}
