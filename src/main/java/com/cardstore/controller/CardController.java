package com.cardstore.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cardstore.dao.CardRepository;
import com.cardstore.dao.UserRepository;
import com.cardstore.entity.BuyResult;
import com.cardstore.entity.Card;
import com.cardstore.entity.CardId;
import com.cardstore.entity.User;

@RestController
public class CardController {

	@Autowired
	CardRepository cardRepository;

	@Autowired
	UserRepository userRepository;

	@RequestMapping(value = "/cards", method = RequestMethod.GET)
	public List<Card> listCards(@RequestParam(name = "inSale", required = false) boolean inSale, HttpSession session) {

		User user = UserController.userfromSession(session);

		if (inSale)
			return (List<Card>) cardRepository.findByInSale(inSale);
		else
			return (List<Card>) cardRepository.findByOwner(user.getUsername());
	}

	@RequestMapping(value = "/cards", method = RequestMethod.POST)
	public Card saveCard(@RequestBody Card card, HttpSession session) {

		User user = UserController.userfromSession(session);

		card.setOwner(user.getUsername());
		card.setDateLoaded(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

		cardRepository.save(card);

		return card;
	}

	// we used {name:.+} instead of {name} to get . in path like
	// '/cards/ceyhun.ozgun'
	@RequestMapping(value = "/cards/{name:.+}", method = RequestMethod.GET)
	public Card getCard(@PathVariable("name") String name, HttpSession session) {
		User user = UserController.userfromSession(session);
		Card card = cardRepository.findOne(new CardId(user.getUsername(), name));

		return card;
	}

	@RequestMapping(value = "/sell", method = RequestMethod.POST)
	public boolean sellCard(@RequestBody Card card, HttpSession session) {
		User user = UserController.userfromSession(session);
		card.setOwner(user.getUsername());

		Card existing = cardRepository.findOne(new CardId(card.getOwner(), card.getName()));

		if (existing != null && !existing.isInSale()) {
			existing.setInSale(true);
			existing.setPrice(card.getPrice());

			cardRepository.save(existing);

			return true;
		}
		return false;
	}

	@RequestMapping(value = "/buy", method = RequestMethod.POST)
	public BuyResult buyCard(@RequestBody Card card, HttpSession session) {
		User user = UserController.userfromSession(session);

		Card cardToBuy = cardRepository.findOne(new CardId(card.getOwner(), card.getName()));

		if (cardToBuy == null || !cardToBuy.isInSale())
			return new BuyResult("Can't buy a non existing or not buyable card.");

		if (cardToBuy.getOwner().equals(user.getUsername()))
			return new BuyResult("Can't buy your own card.");

		// get latest balance from table, do not use the balance in the session
		User currentUser = userRepository.findOne(user.getUsername());
		if (currentUser.getBalance() < cardToBuy.getPrice())
			return new BuyResult("Can't buy a card with price " + cardToBuy.getPrice() + " with a balance "
					+ currentUser.getBalance());

		Card newCard;
		try {
			newCard = (Card) cardToBuy.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException("not expected");
		}

		newCard.setOwner(currentUser.getUsername());
		newCard.setInSale(false);

		// owner field is the hash key of the Card table, so we must delete the
		// old card and save the new card
		cardRepository.delete(cardToBuy);

		cardRepository.save(newCard);

		// transfer the price of the card from buyer to seller
		double newBalance = currentUser.getBalance() - cardToBuy.getPrice();

		currentUser.setBalance(newBalance);
		userRepository.save(currentUser);

		User seller = userRepository.findOne(cardToBuy.getOwner());

		seller.setBalance(seller.getBalance() + cardToBuy.getPrice());
		userRepository.save(seller);

		return new BuyResult(newBalance);
	}
}
