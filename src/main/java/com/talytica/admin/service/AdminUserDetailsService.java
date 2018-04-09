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
		User user = userRepository.findByEmailIgnoreCase(email);
    	log.debug("User Lookup Attempt for: {}, with result {}", email, user);
    	
		if ((user != null) && (user.getUserType() == User.TYPE_SUPER)) return new UserPrincipal(user);
		log.debug("User {} is not a super user", user);
		throw new UsernameNotFoundException(email);
	}
    
}
