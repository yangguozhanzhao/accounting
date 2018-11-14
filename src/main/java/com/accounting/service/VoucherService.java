package com.accounting.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.accounting.dao.VoucherDao;
import com.accounting.entity.Voucher;

@Service
public class VoucherService {
	
	@Autowired
	private VoucherDao voucherDao;
	
    public void save(Voucher voucher) {
    	voucherDao.save(voucher);
    }
    
    public void delete(Long id) {
    	voucherDao.delete(id);
    }
   
    public Voucher findById(Long id) {
    	return voucherDao.findOne(id);
    }
    public Boolean existsById(Long id) {
    	return voucherDao.exists(id);
    }
    
    public Page<Voucher> findRecent() {
    	PageRequest pageable =new PageRequest(0,20,Direction.DESC,"createTime");
    	Page<Voucher> vouchers = voucherDao.findAll(pageable);
        return vouchers;
	}
    
}
