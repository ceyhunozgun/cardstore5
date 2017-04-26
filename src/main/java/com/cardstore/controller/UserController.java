package com.cardstore.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cardstore.dao.UserRepository;
import com.cardstore.entity.User;
import com.cardstore.ses.SESService;
import com.cardstore.sqs.SQSService;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class UserController {

	public static final String USER_KEY_FOR_SESSION = "USER";

	@Autowired
	UserRepository userRepository;

	@Value("${user.activation.queue.name}")
	String userActivationQueueName;

	@Autowired
	SQSService sqsService;

	@Autowired
	SESService sesService;

	@RequestMapping("/")
	public String home(Map<String, Object> model, HttpSession session) {

		User user = userfromSession(session);

		if (user == null)
			return "index";
		else {
			// get up to date balance information from table, because balance
			// can be changed any time.
			User uptoDate = userRepository.findOne(user.getUsername());

			user.setBalance(uptoDate.getBalance());

			model.put("user", user);
			return "dashboard";
		}
	}

	@RequestMapping(value = "/users", method = RequestMethod.POST)
	@ResponseBody
	public boolean registerUser(@RequestBody User user, HttpServletRequest request) {

		User previous = userRepository.findOne(user.getUsername());

		if (previous == null) {
			prepareForActivation(user, makeActivationUrlFromRequest(request, "/users"));

			user.setBalance(100);
			userRepository.save(user);
		}

		return previous == null;
	}

	@RequestMapping(value = "/login", method = RequestMethod.POST, produces = "text/plain")
	@ResponseBody
	public String login(@RequestBody User user, HttpServletRequest request) {
		String error = "None";
		User existing = userRepository.findOne(user.getUsername());

		boolean canLogin = existing != null && existing.getPassword().equals(user.getPassword());

		if (!canLogin)
			error = "User name and password mismatch.";
		else if (!existing.getActivationStatus().equals(User.ACTIVATION_STATUS_DONE))
			error = "User is not activated.";
		else {
			HttpSession session = request.getSession(true);

			session.setAttribute(USER_KEY_FOR_SESSION, existing);
		}
		return error;
	}

	@RequestMapping(value = "/logout", method = RequestMethod.POST)
	@ResponseBody
	public boolean logout(HttpServletRequest request) {
		HttpSession session = request.getSession(false);

		if (session != null)
			session.invalidate();
		return true;
	}

	private String makeActivationUrlFromRequest(HttpServletRequest request, String suffixToReplace) {
		return request.getRequestURL().toString().replace(suffixToReplace, "/activate");
	}

	private void prepareForActivation(User user, String url) {
		user.setActivationToken(String.valueOf(100000 * Math.random()));

		sqsService.sendMessage(userActivationQueueName,
				"{\"username\": \"" + user.getUsername() + "\", \"activationUrl\":\"" + url + "\"}");
	}

	@JmsListener(destination = "${user.activation.queue.name}")
	public void handleUserActivationMailMessage(String json) {
		try {
			ObjectMapper mapper = new ObjectMapper();

			Map<String, String> data = mapper.readValue(json, Map.class);

			String username = data.get("username");
			String activationUrl = data.get("activationUrl");

			User existing = userRepository.findOne(username);

			if (existing != null && !existing.getActivationStatus().equals(User.ACTIVATION_STATUS_DONE)) {
				System.out.println("Sending activation mail for user " + username);

				sendActivationMailForUser(existing, activationUrl);

				existing.setActivationStatus(User.ACTIVATION_STATUS_MAIL_SENT);
				userRepository.save(existing);
			}
		} catch (Exception ex) {
			throw new RuntimeException("Encountered error while processing user activation message.", ex);
		}
	}

	private void sendActivationMailForUser(User user, String activationUrlBase) {

		String activationUrl = activationUrlBase + "?username=" + user.getUsername() + "&token="
				+ user.getActivationToken();

		String to = user.getEmail();
		String subject = "Activate your Digital Card Store account";
		String body = "<html><body><br/>" + "Dear " + user.getName() + "<br/>" + "<a href=\"" + activationUrl
				+ "\">Please click to activate your user account " + user.getUsername() + "</a><br/>"
				+ "</body></html>";

		sesService.sendMessage(to, subject, body);
	}

	@RequestMapping("/activate")
	public String activateUser(Map<String, Object> model, @RequestParam("username") String username,
			@RequestParam("token") String token) {

		User user = userRepository.findOne(username);

		if (user == null)
			model.put("result", "User not found: " + username);
		else if (user.getActivationStatus().equals(User.ACTIVATION_STATUS_DONE))
			model.put("result", "User " + username + " already activated.");
		else if (!user.getActivationToken().equals(token))
			model.put("result", "Activation token for user " + username + " is not correct.");
		else {
			user.setActivationStatus(User.ACTIVATION_STATUS_DONE);
			userRepository.save(user);
			model.put("result", "User " + username + " activated successfully.");
		}
		return "activationResult";
	}

	public static User userfromSession(HttpSession session) {
		User user = (User) session.getAttribute(USER_KEY_FOR_SESSION);

		return user;
	}

	public static boolean loggedIn(HttpSession session) {
		return userfromSession(session) != null;
	}
}
