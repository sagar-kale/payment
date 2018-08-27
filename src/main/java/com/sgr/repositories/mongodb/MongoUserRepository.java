package com.sgr.repositories.mongodb;

import com.sgr.domain.User;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
@Profile("mongodb")
public interface MongoUserRepository extends MongoRepository<User, String> {
}