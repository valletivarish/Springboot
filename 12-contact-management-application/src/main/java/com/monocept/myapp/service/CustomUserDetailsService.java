package com.monocept.myapp.service;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.monocept.myapp.entity.User;
import com.monocept.myapp.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
          User user = userRepository.findByEmail(email)
                 .orElseThrow(() ->
                         new UsernameNotFoundException("User not found with username or email: "+ email));
          
          Set<GrantedAuthority> authorities;
        if(user.isAdmin()) {
        	authorities = Set.of(new SimpleGrantedAuthority("ROLE_ADMIN"));        	
        }
        else {
        	authorities = Set.of(new SimpleGrantedAuthority("ROLE_STAFF"));   
        }


        return new org.springframework.security.core.userdetails.User(user.getEmail(),
                user.getPassword(),
                authorities);
    }
}

