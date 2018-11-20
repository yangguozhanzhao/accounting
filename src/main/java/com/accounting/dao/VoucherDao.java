package com.accounting.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.accounting.entity.User;
import com.accounting.entity.Voucher;

@Repository
public interface VoucherDao extends JpaRepository<Voucher, Long>,JpaSpecificationExecutor<Voucher>{


}
