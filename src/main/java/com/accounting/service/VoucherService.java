package com.accounting.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.accounting.dao.VoucherDao;
import com.accounting.entity.User;
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
    
    
    
}
