package com.cardstore.dao;

import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

import com.cardstore.entity.User;

@EnableScan
public interface UserRepository extends CrudRepository<User, String> {
}