package com.accounting.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.accounting.entity.User;
import com.accounting.entity.Voucher;

@Repository
public interface VoucherDao extends JpaRepository<Voucher, Long>,JpaSpecificationExecutor<User>  {

}
