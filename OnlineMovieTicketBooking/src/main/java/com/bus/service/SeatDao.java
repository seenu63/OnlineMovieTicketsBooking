package com.bus.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bus.beans.OrderHistory;
import com.bus.beans.Seat;

@Component
public class SeatDao {

	@Autowired
	private SeatRepo seatrepo;
	
	

public void productDelete(long sId ) {
	System.out.println(sId+"-----------------------------------2");
	seatrepo.deleteAll();
	
}



public List<Seat> getAll(){
	List<Seat> findAll = seatrepo.findAll();
	return findAll;
}	
}
