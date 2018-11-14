package com.accounting.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.w3c.dom.css.ElementCSSInlineStyle;

import com.accounting.entity.User;
import com.accounting.entity.Voucher;
import com.accounting.service.UserService;

@Controller
public class UserController {
	@Autowired
	private UserService userService;
		
	// 主页
	@RequestMapping(value= {"/","index"})
	public String index(HttpSession session){
		if(userService.findByUsername("admin")==null) {
			User admin = new User("admin","yangzhan");
			User test = new User("杨展","123456");
			userService.save(admin);
			userService.save(test);
		}
		if(session.getAttribute("user")==null) {
			return "index";
		} else {
			return "redirect:/toQuery";
		}
	}
	
	// 用户登录
	@RequestMapping("login")
	public String login(String username,String password,HttpSession session) {
		User user = userService.findByUsernameAndPassword(username, password);
		if(user==null) {
			return "index";
		}else {
			session.setAttribute("user", user);
			return "redirect:/toQuery";
		}
	}
	
	// 用户退出
	@RequestMapping("logout")
	public String logout(HttpSession session) {
		session.removeAttribute("user");
		return "index";
	}

	// 修改密码
	@RequestMapping("user/edit")
	public String editUser(Model model) {
		return "user_edit";
	}
	// 确认修改
	@RequestMapping("user/update")
	public String editUser(User user,HttpSession session) {
		User editUser = userService.findByUsername(user.getUsername());
		editUser.setPassword(user.getPassword());
		userService.save(editUser);
		session.removeAttribute("user");
		return "index";
	}
	
	// 查看所有用户
	@RequestMapping(value = "user/list",method=RequestMethod.GET)
	public String users(Model model) {
		List<User> userList = userService.findUserList();
		model.addAttribute("userList",userList);
		return "user_list";
	}
	
	// 删除用户
	@RequestMapping(value = "user/delete")
	public String delete(Long id) {
		userService.delete(id);
		return "redirect:/user/list";
	}
	
	// 增加用户
	@RequestMapping("user/add")
    public String add(User user) {
        userService.save(user);
        return "redirect:/user/list";
    }
	
}
