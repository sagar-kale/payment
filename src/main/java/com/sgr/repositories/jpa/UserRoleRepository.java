package com.sgr.repositories.jpa;

import com.sgr.domain.Role;
import com.sgr.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface UserRoleRepository extends JpaRepository<Role, Integer> {
    Role findByRole(String role);

}
