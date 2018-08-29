package com.sgr.service;

import com.google.common.collect.Sets;
import com.sgr.domain.CurrentUser;
import com.sgr.domain.Role;
import com.sgr.repositories.jpa.JpaUserRepository;
import com.sgr.repositories.jpa.UserRoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service("userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private JpaUserRepository jpaUserRepository;
    @Autowired
    private UserRoleRepository userRoleRepository;
    private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        com.sgr.domain.User userData = jpaUserRepository.findByUsername(username);
        if (userData == null)
            throw new UsernameNotFoundException("Username not found");
        logger.debug(String.format("Getting details for %s", username));
        //Build user Authority. some how a convert from your custom roles which are in database to spring GrantedAuthority
        List<GrantedAuthority> authorities = buildUserAuthority(userData);

    /*  Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_USER")); */
    /*  String role = user.getRole();
        List<SimpleGrantedAuthority> authList = getAuthorities(role);
        org.springframework.security.core.userdetails.User user = new org.springframework.security.core.userdetails.User(username, encodedPassword, authList);
        return user; */

        //return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), grantedAuthorities);
        return buildUserForAuthentication(userData, authorities);
    }


    private List<GrantedAuthority> buildUserAuthority(com.sgr.domain.User user) {

        boolean flag = false;
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        for (Role role : userRoleRepository.findAll()) {
            if (role.getUsers() != null) {

                for (com.sgr.domain.User userFromrole : role.getUsers()) {
                    System.out.println("In Setting permission Role is not null");
                    System.out.println(String.format("%s : Roles From Role table %s", user.getUsername(), userFromrole.getRole()));
                    if (userFromrole.getUsername().equalsIgnoreCase(user.getUsername())) {
                        System.out.println("***************** before combining ************* \nRole Table Rules::: " + userFromrole.getRoles() + " \nUser Table Rules ::: " + user.getRoles());
                        Set<Role> roles = Sets.union(userFromrole.getRoles(), user.getRoles());
                        System.out.println("***************** after combining ************* \nUnion Rules::: " + roles);
                        for (Role roleFromUser : roles) {
                            if (roleFromUser.getRole() != null && roleFromUser.getRole().trim().length() > 0) {
                                if (roleFromUser.getRole().equalsIgnoreCase("admin")) {
                                    grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                                }
                            }
                        }
                    }
                }
            } else {

                System.out.println("Role Users is  null !! ");
            }
        }

        logger.debug("User role settings");
        System.out.println("user role settings..." + user.getRoles());
        for (Role roleFromUser : user.getRoles()) {
            System.out.println(String.format("User %s : has a role :  %s", user.getUsername(), roleFromUser.getRole()));
            if (roleFromUser.getRole() != null && roleFromUser.getRole().trim().length() > 0) {
                if (roleFromUser.getRole().equalsIgnoreCase("admin")) {
                    grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                }
            }
        }


        //you can also add different roles here
        //for example, the user is also an admin of the site, then you can add ROLE_ADMIN
        //so that he can view pages that are ROLE_ADMIN specific
        System.out.println("Authorization List :: " + grantedAuthorities);

        grantedAuthorities = Sets.union(grantedAuthorities, grantedAuthorities);
        System.out.println("Unique Role Authorization Set :: " + grantedAuthorities);
        return new ArrayList<>(grantedAuthorities);
    }

    private User buildUserForAuthentication(com.sgr.domain.User user, List<GrantedAuthority> authorities) {
        String username = user.getUsername();
        String password = user.getPassword();
        boolean enabled = true;
        boolean accountNonExpired = true;
        boolean credentialsNonExpired = true;
        boolean accountNonLocked = true;

        CurrentUser currentUser = new CurrentUser(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        currentUser.setFirstName(user.getFirstName());
        currentUser.setLastName(user.getLastName());
        currentUser.setEmail(user.getEmail());
        currentUser.setPhone(user.getPhone());

        return currentUser;
    }

    /*private List<GrantedAuthority> buildUserAuthority(Set<UserRole> userRoles) {

        Set<GrantedAuthority> setAuths = new HashSet<GrantedAuthority>();

        // Build user's authorities
        for (UserRole userRole : userRoles) {
            setAuths.add(new SimpleGrantedAuthority(userRole.getRole()));
        }

        return new ArrayList<GrantedAuthority>(setAuths);
    }*/

}
