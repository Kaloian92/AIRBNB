package com.airbnb.controller.user;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;

import javax.servlet.ServletException;
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

	
	
	@RequestMapping(value = {"/login", "/","/index"}, method = {RequestMethod.GET, /*RequestMethod.POST*/})
	public String indexPage() {
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

	@RequestMapping(value = "/home", method = RequestMethod.GET)
	public String homePage(HttpSession session)  {
		if (session.getAttribute("user") == null) {
			return "redirect: ./logout";
		}
		 return "home";
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
			if(userDAO.alreadyExistsUset(email)) {
				throw new InvalidUserException("User already exists.");
			}
			
			
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
			if (!User.validatePassword(password)) {

				throw new InvalidUserException(
						"Your password should be at least 8 characters and must contains at least: one diggit, one upper case letter,one lower case letter and one special character(@#$%^&+=).");
			}
			if (!User.validateEmail(email)) {
				throw new InvalidUserException("You should try with valid email.");
			}
			if (!(User.validateStringText(firstName) || User.validateStringText(lastName))) {
				throw new InvalidUserException("Invalid name, your name should contains only characters.");
			}

			User user = new User(0, email, password, isMale, firstName, lastName, day, month, year, phone);

			userDAO.register(user);

			// session.setAttribute("user", user);
			// session.setMaxInactiveInterval(MAX_TIME);
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

	@RequestMapping(value = "/updateSettings", method = RequestMethod.GET)
	public String settingsPage(HttpSession session) {
		if (session.getAttribute("user") == null) {
			return "redirect: ./logout";
		}
		return "settings";
	}

	@RequestMapping(value = "/updateSettings", method = RequestMethod.POST)
	// @RequestMapping(value = "/updateProfile", method = RequestMethod.POST)
	public String changeSettings(Model model, HttpSession session, @RequestParam String email,
			@RequestParam String phoneNumber, @RequestParam String firstName, @RequestParam String lastName,
			@RequestParam String oldPassword, @RequestParam String newPassword,
			@RequestParam String newPasswordConfirm) {
		try {
			User user = (User) session.getAttribute("user");

			if (!userDAO.comparePasswords(user.getId(), oldPassword)) {
				throw new InvalidUserException("Wrong password.");
			}
			String password = oldPassword;
			if ((newPassword.trim().length() > 0) || (newPasswordConfirm.trim().length() > 0)) {
				if ((newPassword.equals(newPasswordConfirm)) && User.validatePassword(newPassword)) {
					password = newPassword;
				} else {
					throw new InvalidUserException("Wrong new password confirmation.");
				}
			}
			LocalDate bday = user.getBirthdate();
			int day = bday.getDayOfMonth();
			int month = bday.getMonthValue();
			int year = bday.getYear();
			User u = new User(user.getId(), email, password, user.isMale(), firstName, lastName, day, month, year,
					phoneNumber);
			userDAO.updateProfile(u);
			session.setAttribute("user", u);

			return "home";
		} catch (InvalidUserException e) {
			model.addAttribute("exception", e);
			return "error";
		} catch (Exception e) {
			model.addAttribute("exception", e);
			return "error";
		}
	}

//	@RequestMapping(value = "/becomeHost", method = RequestMethod.GET)
//	public String becomeAHost(Model model, HttpSession session) {
//		User user = (User) session.getAttribute("user");
//		try {
//			userDAO.becomeAHost(user);
//			return "redirect:./createPlace";
//		} catch (InvalidUserException e) {
//			model.addAttribute("exception", e);
//			return "error";
//		} catch (Exception e) {
//			model.addAttribute("exception", e);
//			return "error";
//		}
//
//	}

}
