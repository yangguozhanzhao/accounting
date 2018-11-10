package com.accounting.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.w3c.dom.css.ElementCSSInlineStyle;

import com.accounting.entity.User;
import com.accounting.service.UserService;

@Controller
public class LogController {
	@Autowired
	private UserService logService;
	
}
