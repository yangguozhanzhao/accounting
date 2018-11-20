package com.accounting.service;

import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.swing.text.AbstractDocument.Content;
import javax.validation.constraints.Null;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.accounting.dao.LogDao;
import com.accounting.entity.Log;
import com.accounting.entity.User;
import com.accounting.entity.Voucher;

@Service
public class LogService {
	
	@Autowired
	private LogDao logDao;
	
    public void save(Log log) {
    	logDao.save(log);
    }
    
    public Page<Log> findAll(int pageNum,int pageSize) {
    	Sort sort = new Sort(Sort.Direction.DESC, "createTime");
    	return logDao.findAll(new PageRequest(pageNum-1, pageSize,sort));
    }
    
    public Page<Log> findAllByFilter(int pageNum,int pageSize,Date startDate,Date endDate,String content,User user) {
		Sort sort = new Sort(Sort.Direction.DESC, "createTime");
    	return logDao.findAll(new Specification<Log>() {
			@Override
			public Predicate toPredicate(Root<Log> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				// TODO Auto-generated method stub
				Predicate predicate = cb.conjunction();
				
				// 日期范围
				if(startDate != null && endDate!=null) {
					predicate.getExpressions().add(cb.between(root.get("createTime"), startDate, endDate));
				}
				
				// 内容
				if (content != "" && content != null) {
					predicate.getExpressions().add(cb.like(root.get("content"), "%"+content+"%"));
				}
				
				//用户
				if (user != null) {
					predicate.getExpressions().add(cb.equal(root.get("user"), user));
				}
				return predicate;
			}
		},new PageRequest(pageNum-1, pageSize,sort));
	}
    
}
