package com.sgr.service;

import com.sgr.domain.CurrentUser;
import com.sgr.repositories.jpa.JpaUserRepository;
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

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        com.sgr.domain.User userData = jpaUserRepository.findByUsername(username);
        if (userData == null)
            throw new UsernameNotFoundException("Username not found");

        //Build user Authority. some how a convert from your custom roles which are in database to spring GrantedAuthority
        List<GrantedAuthority> authorities = buildUserAuthority(userData.getRole());

    /*  Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_USER")); */
    /*  String role = user.getRole();
        List<SimpleGrantedAuthority> authList = getAuthorities(role);
        org.springframework.security.core.userdetails.User user = new org.springframework.security.core.userdetails.User(username, encodedPassword, authList);
        return user; */

        //return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), grantedAuthorities);
        return buildUserForAuthentication(userData, authorities);
    }

    private List<GrantedAuthority> buildUserAuthority(String role) {

        Set<GrantedAuthority> setAuths = new HashSet<>();
        setAuths.add(new SimpleGrantedAuthority("ROLE_USER"));
        //you can also add different roles here
        //for example, the user is also an admin of the site, then you can add ROLE_ADMIN
        //so that he can view pages that are ROLE_ADMIN specific
        if (role != null && role.trim().length() > 0) {
            if (role.equals("admin")) {
                setAuths.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            }
        }

        return new ArrayList<>(setAuths);
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
