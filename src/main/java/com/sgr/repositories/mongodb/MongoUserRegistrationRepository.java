package com.sgr.repositories.mongodb;

import com.sgr.domain.UserRegistration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
@Profile("mongodb")
public interface MongoUserRegistrationRepository extends MongoRepository<UserRegistration, String> {
}