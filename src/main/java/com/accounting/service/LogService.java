package com.accounting.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.accounting.dao.LogDao;
import com.accounting.entity.Log;
import com.accounting.entity.Voucher;

@Service
public class LogService {
	
	@Autowired
	private LogDao logDao;
	
    public void save(Log log) {
    	logDao.save(log);
    }
    
    public void delete(Long id) {
    	logDao.delete(id);
    }
   
    public Log findById(Long id) {
    	return logDao.findOne(id);
    }
    
}
