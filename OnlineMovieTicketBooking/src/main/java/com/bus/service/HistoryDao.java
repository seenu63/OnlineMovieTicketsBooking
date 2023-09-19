package com.bus.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import com.bus.beans.OrderHistory;

@Component
public class HistoryDao {
	@Autowired
	private HistoryRepo repo;
	
	
	
	
	public List<OrderHistory> getAll(){
		List<OrderHistory> findAll = repo.findAll();
		return findAll;
	}

}
