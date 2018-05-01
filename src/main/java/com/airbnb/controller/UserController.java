package com.airbnb.controller;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.airbnb.exceptions.InvalidUserException;
import com.airbnb.model.user.User;
import com.airbnb.model.user.UserDAO;

@Controller
public class UserController {
	private static final int MAX_TIME = 1800;// seconds

	@Autowired
	private UserDAO userDAO;

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String loginPage() {
		return "index";
	}

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String userLogin(Model model, HttpSession session, @RequestParam String email, @RequestParam String password)
			throws ServletException, IOException {
		try {

			int userId = userDAO.login(email, password);
			User user = null;
			if (userId > 0) {
				user = userDAO.userFromId(userId);
			}
			if (user != null) {
				session.setAttribute("user", user);
				session.setMaxInactiveInterval(MAX_TIME);
				return "home";
			} else {
				throw new InvalidUserException("Invalid username or password");
			}
		} catch (InvalidUserException e) {
			model.addAttribute("exception", e);
			return "error";
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("exception", e);
			return "error";
		}
	}

	@RequestMapping(value = "/register", method = RequestMethod.GET)
	public String getRegisterPage() {
		return "register";
	}

	@RequestMapping(value = "/register", method = RequestMethod.POST)
	protected String userRegister(Model model, HttpSession session, @RequestParam String email,
			@RequestParam boolean isMale, @RequestParam String firstName, @RequestParam String lastName,
			@RequestParam Date bday, @RequestParam String phone, @RequestParam String password,
			@RequestParam String confirmPassword) throws ServletException, IOException {
		try {

			/*
			 * String email = (String) request.getParameter("email"); String password =
			 * (String) request.getParameter("password"); String passwordConfirm = (String)
			 * request.getParameter("confirm password"); boolean isMale =
			 * Boolean.valueOf(String.valueOf(request.getAttribute("gender"))); String
			 * firstName = (String) request.getParameter("first name"); String lastName =
			 * (String) request.getParameter("last name");
			 * 
			 * String birthdate = request.getParameter("bday");
			 */
			Date date = bday;
			LocalDate localDate = date.toLocalDate();
			int month = localDate.getMonthValue();
			int day = localDate.getDayOfMonth();
			int year = localDate.getYear();

			// String phone = request.getParameter("phone");
			System.out.println(localDate.toString());
			if (!password.equals(confirmPassword)) {
				throw new InvalidUserException("Password mismatch");
			}
			if (!User.validateEmail(email)) {
				throw new InvalidUserException("You should try with valid email.");
			}
			if (!(User.validateStringText(firstName) || User.validateStringText(lastName))) {
				throw new InvalidUserException("Invalid name, your name should contains only characters.");
			}

			User user = new User(0, email, password, isMale, firstName, lastName, day, month, year, phone);

			userDAO.register(user);

			session.setAttribute("user", user);
			session.setMaxInactiveInterval(MAX_TIME);
			return "index";

		} catch (InvalidUserException e) {
			e.printStackTrace();
			model.addAttribute("exception", e);
			return "error";
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("exception", e);
			return "error";
		}
	}

	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public String logout(HttpSession session) {

		session.invalidate();

		return "index";

	}

	@RequestMapping(value = "/updateSettings", method = RequestMethod.POST)
	public String changeSettings(Model model, HttpSession session, @RequestParam String email,
			@RequestParam String phoneNumber, @RequestParam String firstName, @RequestParam String lastName,
			@RequestParam String oldPassword, @RequestParam String newPassword,
			@RequestParam String newPasswordConfirm) {
		try {
			User user = (User) session.getAttribute("user");
			if (!email.equals(user.getEmail())) {
				throw new InvalidUserException("Invalid email.");
			}
			if(!userDAO.comparePasswords(user.getId(), oldPassword)) {
				throw new InvalidUserException("Wrong password.");
			}
			userDAO.changePassword(newPassword,newPasswordConfirm, user.getId());
			
			userDAO.changePhoneNumber(user.getId(), phoneNumber);
			
			return "home";
		} catch (InvalidUserException e) {
			model.addAttribute("exception", e);
			return "error";
		}catch(Exception e) {
			model.addAttribute("exception", e);
			return "error";
		}
		
	}
}
