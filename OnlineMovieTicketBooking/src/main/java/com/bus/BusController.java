package com.bus;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpSession;

import org.apache.naming.factory.SendMailFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.bus.beans.Customer;
import com.bus.beans.MovieDetails;
import com.bus.beans.OrderHistory;
import com.bus.beans.Seat;

import com.bus.service.CustomerDao;
import com.bus.service.HistoryDao;
import com.bus.service.SeatDao;
import com.bus.service.movieDao;



@Controller
public class BusController {

	@Autowired
	private CustomerDao dao;
	
	@Autowired
	private HistoryDao hdao;
	
	@Autowired
	private movieDao moviedao;
	@Autowired
	private JavaMailSender mailSenderObj;
	@Autowired
	private SeatDao seatdao;

	
	//opmr
	@GetMapping("/")
	public String shome(Model m, HttpSession session) {

		String movie = (String) session.getAttribute("movieName");
		System.out.println(movie + "========Index");
		List<MovieDetails> movie2 = dao.getAllMovie();
		m.addAttribute("movieList", movie2);
		m.addAttribute("menu", "home");

		return "opening";
	}
	
	
	// Opening home page
	@GetMapping("")
	public String home(Model m, HttpSession session) {

		String movie = (String) session.getAttribute("movieName");
		System.out.println(movie + "========Index");
		List<MovieDetails> movie2 = dao.getAllMovie();
		m.addAttribute("movieList", movie2);
		m.addAttribute("menu", "home");

		return "index";
	}
///---------------------------
	
	
	//Registeration User
		@GetMapping("/register")
		public String register(Model m) {
			
			m.addAttribute("menu", "register");
			return "register";
		}

//		Login form
		@GetMapping("/loginForm")
		public String loginForm(Model m ) {
			
			m.addAttribute("menu", "login");
			return "login";
		}

//		User save process
		@PostMapping("/save")
		public String save(@ModelAttribute("customer")  Customer customer,ModelMap model)throws IOException {
			dao.save(customer);

			return "redirect:/register";

		}

//		Login process
		@PostMapping("/processing")
		public String login(@RequestParam("email") String email, @RequestParam("password") String password,
				HttpSession session, Model m) {

	            	Customer object = (Customer) session.getAttribute("user");
				if (object != null) {
					return "redirect:/booking-seat";
				} else {

					Customer customer = dao.login(email, password);
					
					  if (customer == null || customer.getEmail().equals("")) {
					  m.addAttribute("failed", "Invalied login"); return "login"; }
					 
                   //Admin Mail for Admin Page
					  else if (customer.getEmail().equals("www.seenuseenu004@gmail.com")) {
						session.setAttribute("user", customer);
						return "AdminHome";
					}
			        else {
						session.setAttribute("user", customer);
					}
					
					return "redirect:/home";
				}
			
			}
		
		
	
//Admin PAge------------------------------------------
	@RequestMapping("/AdminHome")
	public String ownerHome() {
		return "AdminHome";
	}
	
	@RequestMapping("/saveProduct")
	public String saveAll(@ModelAttribute("MovieDetails") MovieDetails m) {
		moviedao.saveProduct(m);
		return "AdminHome";
	}	
	
	@RequestMapping("/view")
	public ModelAndView viewAll(MovieDetails p) {
		List<MovieDetails> flims=moviedao.getMovie();
		return new ModelAndView("view").addObject("flims",flims);}
	
	
	
	@RequestMapping("/orders")
	public ModelAndView OrderviewAll(OrderHistory p) {
		
		List<OrderHistory> flims=hdao.getAll();
		
		return new ModelAndView("AllOrders").addObject("flims",flims);} 
	
	
	
@RequestMapping("/deleteProduct")	
public String delete(@RequestParam Long movieId) {
	moviedao.productDelete(movieId);
	return "redirect:/view";
}


//user Page Customer


@GetMapping("/home")
public String mainDashboard(HttpSession session, Model m) {
	session.removeAttribute("bookingdate");
	session.removeAttribute("bookingtime");
	session.removeAttribute("movieName");
	m.addAttribute("menu", "home");

	String message = (String) session.getAttribute("msg");
	m.addAttribute("message", message);
	session.removeAttribute("msg");
//	System.out.println(message);
	List<MovieDetails> movie2 = dao.getAllMovie();
	m.addAttribute("listMovie", movie2);

	return "main-dashboard";
}




	//-----------------
//second page
	@GetMapping("/booking")
	public String bookingCheck(@RequestParam("movieName") String movieName, Model m, HttpSession session) {
	
		List<MovieDetails> movie2 = dao.getAllMovie();
		List<String> checkMovie = new ArrayList<>();
		for (MovieDetails string : movie2) {
			checkMovie.add(string.getMovieName());
		}
		if (checkMovie.contains(movieName)) {
			session.setAttribute("movieName", movieName);
			System.out.println(movieName);
			LocalDate now = LocalDate.now();
			LocalDate monthLimit = LocalDate.now();
			String time = "09:00 am";
			List<String> seatNo1 = new ArrayList<String>();
			List<Seat> all = dao.getAllSeat(now, time);

			for (Seat s : all) {
				for (String s1 : s.getSeatNo()) {
					seatNo1.add(s1);
				}

			}

			m.addAttribute("date", now);
			m.addAttribute("max", monthLimit.plusMonths(1));
			m.addAttribute("min", monthLimit);
			m.addAttribute("time", time);
			m.addAttribute("seats", seatNo1);
			return "home";

		} else {
			return "redirect:/";

		}

	}


	
	


//	Dashboard page
	@GetMapping("/booking-seat")
	public String getUser(@RequestParam("movieName") String movieName, HttpSession session, Model m) {
		
		List<MovieDetails> movie2 = dao.getAllMovie();
		List<String> checkMovie = new ArrayList<>();
		for (MovieDetails string : movie2) {
			checkMovie.add(string.getMovieName());
		}
		if (checkMovie.contains(movieName)) {
			session.setAttribute("movieName", movieName);

			LocalDate now = LocalDate.now();
			LocalDate monthLimit = LocalDate.now();
			String time = "09:00 am";

			Customer customer = (Customer) session.getAttribute("user");
			List<String> seatNo1 = new ArrayList<String>();
			List<Seat> seat = customer.getSeat();

			List<Seat> all = dao.getAllSeat(now, time);

			for (Seat s : all) {
				for (String s1 : s.getSeatNo()) {
					seatNo1.add(s1);
				}

			}

			m.addAttribute("date", now);
			m.addAttribute("time", time);
			m.addAttribute("max", monthLimit.plusMonths(1));
			m.addAttribute("min", monthLimit);
			m.addAttribute("seats", seatNo1);
			m.addAttribute("seat", seat);
			session.setAttribute("user", customer);
			return "dashboard";
		} else {
			return "redirect:/home";
		}

	}



//	Seat booking process
	@PostMapping("/book-seat")
	public String bookSeat(@ModelAttribute("Seat") Seat seat, @RequestParam("movieName") String movieName,
			HttpSession session, Model m){
		
		LocalDate currentDate = LocalDate.now();
		ZoneId defaultZoneId = ZoneId.systemDefault();
		Date todayDate = Date.from(currentDate.atStartOfDay(defaultZoneId).toInstant());
		LocalDate date = (LocalDate) session.getAttribute("bookingdate");
		String time = (String) session.getAttribute("bookingtime");
		System.out.println(seat.getSeatNo().equals(null) + " wooo" + movieName.equals(null));
		Customer object = (Customer) session.getAttribute("user");

		if (object == null) {
			return "redirect:/loginForm";
		} else if ((seat.getSeatNo().isEmpty()) && (movieName.equals(null))) {
			System.out.println("Seat is null");
			return "redirect:/home";
		} else if (date == null) {
			date = currentDate;
			time = "09:00 am";
			if (((date.isAfter(currentDate)) || (date.equals(currentDate)))
					&& (date.isBefore(currentDate.plusMonths(1)) || date.equals(currentDate.plusMonths(1)))) {

				Date date2 = Date.from(date.atStartOfDay(defaultZoneId).toInstant());
				List<Double> price = new ArrayList<Double>();
				double sum = 0;
				double p = 150.22d;
				for (String s : seat.getSeatNo()) {
					sum = sum + p;
					price.add(p);
				}
				seat.setTotal(sum);
				seat.setPrice(price);

				OrderHistory history = new OrderHistory(seat.getSeatNo(), price, sum, movieName, todayDate, date2, time,
						object);
				dao.saveSeat(seat, object, date2, time);
				dao.saveHistory(history, object);
				List<String> seatNo1 = new ArrayList<String>();
				List<Customer> all = dao.getAll();
				for (Customer c : all) {
					for (Seat s : c.getSeat()) {
						for (String s1 : s.getSeatNo()) {
							seatNo1.add(s1);
						}

					}
				}

				m.addAttribute("seats", seatNo1);
				session.setAttribute("user", object);
				
				//mail part to send datas of customer and movies details
			   sendmail(history);
				session.setAttribute("msg", "your seat book successsfully");
				return "redirect:/home";

			} else {
				System.out.println("ye date current date se pahle ki date hai");
				return "redirect:/booking-seat?movieName=" + movieName;

			}
		} else {
			if (((date.isAfter(currentDate)) || (date.equals(currentDate)))
					&& (date.isBefore(currentDate.plusMonths(1)) || date.equals(currentDate.plusMonths(1)))) {
				Date date2 = Date.from(date.atStartOfDay(defaultZoneId).toInstant());
				List<Double> price = new ArrayList<Double>();
				double sum = 0;
				double p = 150.22d;
				for (String s : seat.getSeatNo()) {
					sum = sum + p;
					price.add(p);
				}
				seat.setTotal(sum);
				seat.setPrice(price);

				OrderHistory history = new OrderHistory(seat.getSeatNo(), price, sum, movieName, todayDate, date2, time,
						object);
				dao.saveSeat(seat, object, date2, time);
				dao.saveHistory(history, object);
				List<String> seatNo1 = new ArrayList<String>();
				List<Customer> all = dao.getAll();
				for (Customer c : all) {
					for (Seat s : c.getSeat()) {
						for (String s1 : s.getSeatNo()) {
							seatNo1.add(s1);
						}

					}
				}

				m.addAttribute("seats", seatNo1);
				session.setAttribute("user", object);
				
			//	sendmail();
				session.setAttribute("msg", "your seat book successsfully ");
				return "redirect:/home";

			} else {
				System.out.println("ye date current date se pahle ki date hai");
				return "redirect:/booking-seat?movieName=" + movieName;

			}
		}

		
	}
	
	
	//Mail send processing
	public void sendmail(OrderHistory his) {

        Customer mail=his.getCustomer(); 
		final String emailToRecipient =mail.getEmail();
		final String emailSubject = "Movie Booked ";
		final String emailMessage1 = "<html> <body> <p>Dear Customer,</p><p>"+ " Id: "+mail.getBid()+",<br>Name: "+mail.getName()+",<br>Booked Date: "+his.getBookOnDate()+",<br>Movie: "+his.getMovieName()+",<br>Total Price: "+his.getTotal()+",<br>Time: "+his.getShowTime()+",<br>Seat no: "+his.getSeat() +"</p> <p>You have succesfully Registered with our  <b>Moon Cinema</b></p><p>Thank You</p></body></html>";
       // System.out.println(" Id: "+mail.getBid()+",Name: "+mail.getName()+",Booked Date: "+his.getBookOnDate()+",Movie: "+his.getMovieName()+",TotalTicket: "+his.getTotal()+",Price: "+his.getPrice()+",Time: "+his.getShowTime()+"Seat no: "+his.getSeat());
		mailSenderObj.send(new MimeMessagePreparator() {
			public void prepare(MimeMessage mimeMessage) throws Exception {

				MimeMessageHelper mimeMsgHelperObj = new MimeMessageHelper(mimeMessage, true, "UTF-8");
				mimeMsgHelperObj.setTo(emailToRecipient);
				
				mimeMsgHelperObj.setText(emailMessage1, true);

				mimeMsgHelperObj.setSubject(emailSubject);
				
				//mimeMsgHelperObj.addAttachment(file.getOriginalFilename(), file);
			}
		});

	}

//	Order history
	@GetMapping("/order-history")
	public String history(HttpSession session, Model m) {
		Date todayDate = new Date();
		Customer object = (Customer) session.getAttribute("user");
		session.setAttribute("user", object);
		List<OrderHistory> list = dao.getAllHistory(object.getBid());
		m.addAttribute("hList", list);
		m.addAttribute("todaydate", todayDate);

		LocalDate date = (LocalDate) session.getAttribute("bookingdate");
		System.out.println(date);
		m.addAttribute("menu", "order");
		return "history";
	}
	
	
	

//	Admin power to clear All seats
	@GetMapping("/clear-seats")
	public String eraseSeat(HttpSession session) {
		LocalDate now = LocalDate.now();
		String time = "09:00 am";
		Customer object = (Customer) session.getAttribute("user");

		if (object != null) {
			List<Seat> list = dao.getAllSeat(now, time);
			for (Seat seat : list) {
				long id = seat.getsId();
				dao.delete(id);
			}

		}
		return "redirect:/booking-seat";
	}

//	Admin can see all Customers
	@GetMapping("/all-customers-records")
	public String allRecords(Model m, HttpSession session) {
		Customer object = (Customer) session.getAttribute("user");
		long bid = object.getBid();
		if (bid == 1) {
			List<Customer> all = dao.getAll();
			m.addAttribute("records", all);
			m.addAttribute("menu", "allusers");
			return "user_records";
		} else {
			return "redirect:/booking-seat";
		}
	}

//	Admin can see all Customers and their seats
	@GetMapping("/all-seats/{id}")
	public String allSeats(@PathVariable("id") long id, Model m, HttpSession session) {
		Customer object = (Customer) session.getAttribute("user");
		long bid = object.getBid();
		if (bid == 1) {
			List<OrderHistory> list = dao.getAllHistory(id);
			m.addAttribute("seatRecords", list);
			m.addAttribute("menu", "allusers");
			return "seat-records";
		} else {
			return "redirect:/booking-seat";
		}

	}

//	User Setting
	@GetMapping("/setting")
	public String getSetting(Model m, HttpSession session) {
		Customer customer = (Customer) session.getAttribute("user");
		m.addAttribute("user", customer);
		m.addAttribute("menu", "setting");
		return "setting";
	}

//	User update form
	@GetMapping("/setting/update/{id}")
	public String updateForm(@PathVariable("id") long id, Model m) {
		System.out.println(id);
		m.addAttribute("menu", "setting");
		return "update-details";

	}

//	update Details
	@PostMapping("/setting/update-details")
	public String updateDetails(@ModelAttribute("customer") Customer cust, HttpSession session) {
		String name = cust.getName();
		String email = cust.getEmail();
		Customer customer = (Customer) session.getAttribute("user");
		customer.setName(name);
		customer.setEmail(email);
		dao.updateDetail(customer);

		return "redirect:/setting";

	}

	@PostMapping("/check")
	public String checkDate(@RequestParam("localdate") String date, @RequestParam("localtime") String time, Model m,
			HttpSession session) {
		Customer object = (Customer) session.getAttribute("user");
		String movie = (String) session.getAttribute("movieName");
		LocalDate monthLimit = LocalDate.now();
		if (movie.equals(null)) {
			return "home";

		} else if (object == null) {
			LocalDate now = LocalDate.parse(date);
			List<String> seatNo1 = new ArrayList<String>();
			List<Seat> all = dao.getAllSeat(now, time);

			for (Seat s : all) {
				for (String s1 : s.getSeatNo()) {
					seatNo1.add(s1);
				}

			}

			session.setAttribute("bookingdate", now);
			session.setAttribute("bookingtime", time);
			m.addAttribute("date", now);
			m.addAttribute("max", monthLimit.plusMonths(1));
			m.addAttribute("min", monthLimit);
			m.addAttribute("time", time);
			m.addAttribute("seats", seatNo1);

			return "home";
		} else {
			LocalDate now = LocalDate.parse(date);
			List<String> seatNo1 = new ArrayList<String>();
			List<Seat> all = dao.getAllSeat(now, time);

			for (Seat s : all) {
				for (String s1 : s.getSeatNo()) {
					seatNo1.add(s1);
				}

			}

			session.setAttribute("bookingdate", now);
			session.setAttribute("bookingtime", time);
			m.addAttribute("date", now);
			m.addAttribute("max", monthLimit.plusMonths(1));
			m.addAttribute("min", monthLimit);
			m.addAttribute("time", time);
			m.addAttribute("seats", seatNo1);

			return "dashboard";
		}

	}

//	Exception handling
	@ExceptionHandler(Exception.class)
	public String handleError(Exception ex, Model m, HttpSession session) {
		Customer object = (Customer) session.getAttribute("user");
		if (object == null) {
			return "redirect:/loginForm";
		} else {
			return "redirect:/home";
		}
	}
	
	
//	Logout process
	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.removeAttribute("user");

		session.removeAttribute("bookingdate");
		session.removeAttribute("bookingtime");
		session.removeAttribute("movieName");

		return "redirect:/";
	}

}
