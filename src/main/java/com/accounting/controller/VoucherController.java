package com.accounting.controller;

import java.io.BufferedOutputStream;
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

import com.accounting.entity.Report;
import com.accounting.entity.User;
import com.accounting.entity.Voucher;
import com.accounting.service.VoucherService;

import ch.qos.logback.core.joran.conditional.ElseAction;

@Controller
public class VoucherController {
	@Autowired
	private VoucherService voucherService;
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
	public String scan() {
		return "voucher_add";
	}

	// 添加凭证
	@PostMapping("voucher/add")
	@ResponseBody
	public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file,
			@RequestParam(value = "content", defaultValue = "无") String content,
			@RequestParam("recordDate") String recordDate, HttpSession session) {
		logger.info("try to add voucher");
		if (!file.isEmpty()) {
			try {
				// 上传文件路径,按照系统日期分文件夹存储
				String imagePath = "/upload/images/";
				String uploudDir = CLASSPATH + imagePath;
				uploudDir = uploudDir.replace('\\', '/');
				logger.info("path = " + uploudDir);
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
				File fileDir = new File(uploudDir, filename);
				if (!fileDir.getParentFile().exists()) {
					fileDir.getParentFile().mkdirs();
				}
				// 将上传文件保存到一个目标文件当中
				file.transferTo(new File(uploudDir + filename));
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
				try {
					Date date = sdf.parse(recordDate);
					User user = (User) session.getAttribute("user");
					Voucher voucher = new Voucher(date, imagePath + filename, content, user);
					voucherService.save(voucher);
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
		return ResponseEntity.ok().header("Content-Type", "text/plain").body("no refresh");
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
	public String showRecentVouchers(Model model) {
		Page<Voucher> voucherList = voucherService.findRecent();
		model.addAttribute("recentVouchers", voucherList);
		return "query";
	}
	
	// 查询日报或者凭证
	@RequestMapping(value = "query", method = RequestMethod.POST)
	public String query(Model model,HttpServletRequest request) {
		logger.info("query");
		String date1=request.getParameter("recordDate1");
		String date2=request.getParameter("recordDate2");
		String key=request.getParameter("key");
		String radio=request.getParameter("optionsRadios");
		logger.info(date1+date2+key+radio);
		
		Page<Voucher> voucherList = voucherService.findRecent();
		model.addAttribute("recentVouchers", voucherList);
		return "query";
	}

	// 根据id显示详情
	@GetMapping("voucher/voucher/{id}")
	public String voucher(Model model, @PathVariable("id") Long id) {
		Voucher voucher = voucherService.findById(id);
		model.addAttribute("voucher", voucher);
		return "voucher";
	}
	
	// 根据id显示详情
	@GetMapping("voucher/prev/{id}")
	public String prev(Model model, @PathVariable("id") Long id) {
		
		// 只能搜到10以内
		int n = 10;
		id=id-1;
		while(!voucherService.existsById(id) && n>0) {
			id=id-1;
			n=n-1;
		}
		Voucher voucher = voucherService.findById(id);
		model.addAttribute("voucher", voucher);
		return "redirect:/voucher/voucher/"+id;
	}
	
	// 根据id显示详情
		@GetMapping("voucher/next/{id}")
		public String next(Model model, @PathVariable("id") Long id) {
			
			// 只能搜到10以内
			int n = 10;
			id=id+1;
			while(!voucherService.existsById(id) && n>0) {
				id=id+1;
				n=n-1;
			}
			Voucher voucher = voucherService.findById(id);
			model.addAttribute("voucher", voucher);
			return "redirect:/voucher/voucher/"+id;
		}
	

	// 删除
	public String report(@PathVariable("id") Long id) {
		voucherService.delete(id);
		return "redirect:/toQuery";
	}

}
