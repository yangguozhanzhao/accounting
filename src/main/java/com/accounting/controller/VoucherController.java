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

import com.accounting.entity.Voucher;
import com.accounting.service.VoucherService;

import ch.qos.logback.core.joran.conditional.ElseAction;

@Controller
@RequestMapping("voucher")
public class VoucherController {
	@Autowired
	private VoucherService voucherService;
    private Logger logger = LoggerFactory.getLogger(getClass());

	
	// 扫描凭证页面
	@GetMapping("/toAdd")
	public String scan() {
		return "voucher_add";
	}

	@PostMapping("/add")
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
    		@RequestParam(value="content",defaultValue="无") String content,
    		@RequestParam("recordDate") String recordDate){
      
		if(!file.isEmpty()) {
			String imagePath = "iamges/"+file.getOriginalFilename();
			logger.info("upload...");
			logger.info(imagePath);
			logger.info(recordDate);
			logger.info(content);
			try {
				BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(new File(imagePath)));
				out.write(file.getBytes());
				out.flush();
				out.close();
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
		return "voucher_add";
	}
	

	
	// 根据id显示详情
	@RequestMapping(value="voucher",method=RequestMethod.GET)
	public Voucher  show(Long id) {
		return voucherService.findById(id);
	}
	
}
