package com.accounting.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.accounting.entity.Log;
import com.accounting.entity.User;

@Repository
public interface LogDao extends JpaRepository<Log, Long> {


}