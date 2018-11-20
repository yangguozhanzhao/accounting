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

import com.accounting.dao.VoucherDao;
import com.accounting.entity.Voucher;

@Service
public class VoucherService {

	@Autowired
	private VoucherDao voucherDao;
	private Logger logger = LoggerFactory.getLogger(getClass());


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
		PageRequest pageable = new PageRequest(0, 20, Direction.DESC, "createTime");
		Page<Voucher> vouchers = voucherDao.findAll(pageable);

		return vouchers;
	}

	public Page<Voucher> findByDateAndContent(int pageNum, int pageSize, Date startDate,
			Date endDate, String content) {
		Sort sort = new Sort(Sort.Direction.DESC, "createTime");

		return voucherDao.findAll(new Specification<Voucher>() {

			@Override
			public Predicate toPredicate(Root<Voucher> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate predicate = cb.conjunction();

				// 日期范围
				if (startDate != null && endDate!=null) {
					logger.info("date="+startDate+endDate);
					predicate.getExpressions().add(cb.between(root.get("recordDate"), startDate, endDate));
				}
				if (content != "" && content != null) {
					logger.info("content="+content);
					predicate.getExpressions().add(cb.like(root.get("content"), "%"+content+"%"));
				}
				return predicate;
			}
		}, new PageRequest(pageNum - 1, pageSize,sort));
	}

}
