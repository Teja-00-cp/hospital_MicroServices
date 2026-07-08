package com.example.order.Service; // Adjust to your package

import com.example.order.Model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserSer userSer; // Hooks directly to your user database service layer

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User dbUser = userSer.findByUsername(username); 
        if (dbUser == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }

        // Get the plain string value of your Role enum (e.g., "PATIENT")
        String roleString = (dbUser.getRole() != null) ? dbUser.getRole().name() : "PATIENT";

        return new org.springframework.security.core.userdetails.User(
                dbUser.getUsername(),
                dbUser.getPassword(), // Your BCrypt hashed password from the database
                Collections.singletonList(new SimpleGrantedAuthority(roleString))
        );
    }
}