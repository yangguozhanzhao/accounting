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

import org.codehaus.groovy.ast.stmt.LoopingStatement;
import org.hibernate.boot.archive.scan.spi.ScanEnvironment;
import org.hibernate.validator.internal.util.privilegedactions.GetClassLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.w3c.dom.css.ElementCSSInlineStyle;

import com.accounting.entity.User;
import com.accounting.entity.Voucher;
import com.accounting.service.VoucherService;

import ch.qos.logback.core.joran.conditional.ElseAction;

@Controller
public class VoucherController {
	@Autowired
	private VoucherService voucherService;
    private Logger logger = LoggerFactory.getLogger(getClass());

	
	// 扫描凭证页面
	@GetMapping("voucher/toAdd")
	public String scan() {
		return "voucher_add";
	}

	// 添加凭证
	@PostMapping("voucher/add")
	@ResponseBody
    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file,
    		@RequestParam(value="content",defaultValue="无") String content,
    		@RequestParam("recordDate") String recordDate,HttpServletRequest request){
		logger.info("try to add voucher");
		if(!file.isEmpty()) {
			String imagePath = "upload/images/"+file.getOriginalFilename();
			logger.info(imagePath);
			logger.info(recordDate);
			logger.info(content);
			try {
				// 上传文件路径,按照系统日期分文件夹存储
				String path = request.getServletContext().getRealPath("/upload/");
				logger.info("path = " + path);
				// 上传文件名
				String filename = file.getOriginalFilename();
				File filepath = new File(path,filename);
				// 判断路径是否存在，如果不存在就创建一个
				if (!filepath.getParentFile().exists()) { 
					filepath.getParentFile().mkdirs();
				}
				// 将上传文件保存到一个目标文件当中
				file.transferTo(new File(path+File.separator+ filename));
				
				//BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(new File(imagePath)));
				//out.write(file.getBytes());
				//out.flush();
				//out.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				logger.error("无文件");
			} catch (IOException e) {
				e.printStackTrace();
				// TODO: handle exception
				logger.error("图片保存失败");
			}
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
			try {  
				Date date = sdf.parse(recordDate);
				Voucher voucher = new Voucher(date,imagePath,content);
				voucherService.save(voucher);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				logger.error("凭证保存失败");
				e.printStackTrace();
			}
		}
		return ResponseEntity.ok().header("Content-Type", "text/plain").body("no refresh");

	}
	
	// 查询凭证页面前10
	@RequestMapping(value="query",method=RequestMethod.GET)
	public String showRecentVouchers(Model model) {
		Page<Voucher> voucherList = voucherService.findRecent();
		model.addAttribute("recentVouchers",voucherList);
		return "query";
	}
	
	
	// 根据id显示详情
	@RequestMapping(value="",method=RequestMethod.GET)
	public Voucher  show(Long id) {
		return voucherService.findById(id);
	}
	
}
