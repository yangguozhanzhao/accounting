package com.accounting.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.w3c.dom.css.ElementCSSInlineStyle;

import com.accounting.dao.UserDao;
import com.accounting.entity.Log;
import com.accounting.entity.User;
import com.accounting.service.LogService;
import com.accounting.service.UserService;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

@Controller
public class LogController {
	@Autowired
	private LogService logService;
	
	@Autowired
	private UserService userService;
	
	private Logger logger = LoggerFactory.getLogger(getClass());

	@RequestMapping(value="logQuery")
	public String query(Model model,HttpServletRequest request,HttpSession session) throws ParseException {
		
		if(session.getAttribute("user")==null) {
			return "index";
		} else {
			String date1=request.getParameter("startDate");
			String date2=request.getParameter("endDate");
			String pageNum=request.getParameter("pageNum");
			String content=request.getParameter("content");
			String username = request.getParameter("username");
			logger.info("try to get log = "+username);
			User user = userService.findByUsername(username);
			int pageSize =20;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
			logger.info(date1,date2);
			if (date1!=null && date2!=null) {
			Date startDate = sdf.parse(date1);
			Date endDate =sdf.parse(date2);
			logger.info("pra="+pageNum+pageSize+startDate+endDate+content+user);
			Page<Log> lPage=logService.findAllByFilter(Integer.parseInt(pageNum), pageSize, startDate, endDate, content, user);
			model.addAttribute("lPage",lPage);
			model.addAttribute("startDate",date1);
			model.addAttribute("endDate",date2);
			model.addAttribute("content",content);
			model.addAttribute("username",username);
			}
			return "log";
		}
	}
	
	@RequestMapping(value="log/{page}")
	public String allLog(@PathVariable("page") int page,Model model,HttpSession session) {
		
		if(session.getAttribute("user")==null) {
			return "index";
		} else {
			Page<Log> allPage= logService.findAll(page, 20);
			model.addAttribute("allPage",allPage);
			return "log";
		}
		
	}
}
