package com.accounting.service;

import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.accounting.dao.ReportDao;
import com.accounting.entity.Report;
import com.accounting.entity.Voucher;

@Service
public class ReportService {
	
	@Autowired
	private ReportDao reportDao;
	private Logger logger = LoggerFactory.getLogger(getClass());

	
    public void save(Report Report) {
    	reportDao.save(Report);
    }
    
    public void delete(Long id) {
    	reportDao.delete(id);
    }
   
    public Report findById(Long id) {
    	return reportDao.findOne(id);
    }
    
    public Page<Report> findRecent() {
    	PageRequest pageable =new PageRequest(0,10,Direction.DESC,"createTime");
    	Page<Report> reports = reportDao.findAll(pageable);
        return reports;
	}
    
    public Page<Report> findByDateR(int pageNum,int pageSize,Date startDate,Date endDate) {
    	logger.info("date="+startDate+endDate);
    	Sort sort = new Sort(Sort.Direction.DESC, "createTime");
		return reportDao.findAll(new Specification<Report>() {
			
			@Override
			public Predicate toPredicate(Root<Report> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				// TODO Auto-generated method stub
				logger.info("date="+startDate+endDate);
				 Predicate predicate = cb.conjunction();
				 //日期范围
				 if (startDate!=null && endDate!=null) {
					predicate.getExpressions().add(cb.between(root.get("recordDate"), startDate, endDate));
				}
				 return predicate;
			}
		}, new PageRequest(pageNum-1, pageSize, sort));
		
	}
    
}
