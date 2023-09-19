package com.bus.beans;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;

import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "flim_detail")
public class MovieDetails {
	
	@Id
	@GenericGenerator(name="ref",strategy="increment")
	@GeneratedValue(generator="ref")
	@Column(name="movie_id")
	private long movieId;
	
	@Column
	private String movieName;
	
	@Column
	private String image;
	
	@Column
	private String movieDetails;
	@Column
	private String screenName;

	public long getMovieId() {
		return movieId;
	}

	public void setMovieId(long movieId) {
		this.movieId = movieId;
	}

	public String getMovieName() {
		return movieName;
	}

	public void setMovieName(String movieName) {
		this.movieName = movieName;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getMovieDetails() {
		return movieDetails;
	}

	public void setMovieDetails(String movieDetails) {
		this.movieDetails = movieDetails;
	}

	
	
	public String getScreenName() {
		return screenName;
	}

	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}

	public MovieDetails(long movieId, String movieName, String image, String movieDetails,String screenName) {
		super();
		this.movieId = movieId;
		this.movieName = movieName;
		this.image = image;
		this.movieDetails = movieDetails;
		this.screenName=screenName;
	}

	public MovieDetails(String movieName, String image, String movieDetails,String screenName) {
		super();
		this.movieName = movieName;
		this.image = image;
		this.movieDetails = movieDetails;
		this.screenName=screenName;
	}

	public MovieDetails() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		return "MovieDetails [movieId=" + movieId + ", movieName=" + movieName + ", image=" + image + ", movieDetails="
				+ movieDetails + ", screenName=" + screenName + "]";
	}



}
