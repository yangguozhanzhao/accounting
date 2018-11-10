package com.accounting.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.accounting.dao.ReportDao;
import com.accounting.entity.Report;

@Service
public class ReportService {
	
	@Autowired
	private ReportDao reportDao;
	
    public void save(Report Report) {
    	reportDao.save(Report);
    }
    
    public void delete(Long id) {
    	reportDao.delete(id);
    }
   
    public Report findById(Long id) {
    	return reportDao.findOne(id);
    }
    
}
