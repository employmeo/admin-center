package com.talytica.admin.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.employmeo.data.model.User;
import com.employmeo.data.repository.UserRepository;
import com.talytica.admin.objects.UserPrincipal;

@Service
public class AdminUserDetailsService implements UserDetailsService {
	private static final Logger log = LoggerFactory.getLogger(AdminUserDetailsService.class);
	
	@Autowired
	private UserRepository userRepository;

    @Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    	log.debug("User Lookup Attempt for: {}", email);
		User user = userRepository.findByEmail(email);
		if (user != null) {
	    	log.debug("Found User: {}", user);
			return new UserPrincipal(user);
		} else {
		    throw new UsernameNotFoundException(email);
		}
	}
    
}
