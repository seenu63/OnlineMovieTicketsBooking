package com.bus.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bus.beans.MovieDetails;



@Component
public class movieDao {

	@Autowired
	private MovieRepo movieRepo;
	
	public void saveProduct(MovieDetails m) {
		movieRepo.save(m);
	}
	
	public List<MovieDetails> getMovie(){
		List<MovieDetails> allproduct=(List<MovieDetails>) movieRepo.findAll();
		return allproduct;

	}
	

public void productDelete(long pid) {
	movieRepo.deleteById(pid);
	
}
	
	
}
