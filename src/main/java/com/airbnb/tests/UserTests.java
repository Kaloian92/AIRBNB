package com.airbnb.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.airbnb.config.SpringWebConfig;
import com.airbnb.exceptions.DBException;
import com.airbnb.exceptions.InvalidUserException;
import com.airbnb.model.db.DBConnectionTest;
import com.airbnb.model.user.User;
import com.airbnb.model.user.UserDAO;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {UserDAO.class,DBConnectionTest.class})
//@ContextConfiguration(classes=SpringWebConfig.class)
public class UserTests {
	
	@Autowired
	private UserDAO dao;
	
	@Test(expected = InvalidUserException.class)
	public void testBadPassword() throws  InvalidUserException, DBException {
		dao.login("User", "pasWo=1");
	}
	
	@Test(expected = InvalidUserException.class)
	public void testBadUsername() throws  InvalidUserException, DBException {
		dao.login("Admin","");
	}

	@Test
	public void testSuccess() throws InvalidUserException {
		int id = dao.login("a@mail.bg","parolKa=23");
		assertEquals(id, 12);
	}
	
	@Test
	public void registerUser()throws InvalidUserException{
		int id = dao.register(new User(0, "junit@com.bg", "parolKa=23", false, "Junit", "Test", 2, 3, 1987, "0883332333"));
		assertTrue(id > 0);
	}
	
	
}
