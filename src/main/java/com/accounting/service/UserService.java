package com.accounting.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.accounting.dao.UserDao;
import com.accounting.entity.User;

@Service
public class UserService {
    @Autowired
    private UserDao userDao ;
    

    public void save(User user) {
    	userDao.save(user);
    }
    
    public void delete(Long id) {
        userDao.delete(id);
    }
    
    public User findByUsernameAndPassword(String username,String password) {
        return userDao.findByUsernameAndPassword(username, password);
    }
    
    public User findByUsername(String username) {
        return userDao.findByUsername(username);
    }
    
    public List<User> findUserList() {
        return userDao.findAll();
    }
}
