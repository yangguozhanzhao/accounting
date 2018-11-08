package com.accounting.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.accounting.entity.Voucher;

@Repository
public interface VoucherDao extends JpaRepository<Voucher, Long>  {

}
