package com.cardstore.dao;

import java.util.List;

import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.cardstore.entity.Card;
import com.cardstore.entity.CardId;

@EnableScan
public interface CardRepository extends CrudRepository<Card, CardId> {
	List<Card> findByOwner(String owner);

	List<Card> findByInSale(@Param("inSale") boolean inSale);
}
