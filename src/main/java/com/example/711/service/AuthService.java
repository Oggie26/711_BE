package com.example._1.service;

import com.example._1.entity.Account;
import com.example._1.entity.Role;
import com.example._1.entity.UserProfile;
import com.example._1.repository.AccountRepository;
import com.example._1.repository.UserProfileRepository;
import com.example._1.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AccountRepository accountRepository;
    private final UserProfileRepository userProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @Transactional
    public String register(String username, String password, Role role) {
        if (accountRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists");
        }

        Account account = Account.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .role(role != null ? role : Role.USER)
                .build();

        UserProfile profile = UserProfile.builder()
                .account(account)
                .loyaltyPoints(0)
                .build();
        
        account.setUserProfile(profile);

        accountRepository.save(account);
        return "User registered successfully";
    }

    public String login(String username, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        return jwtUtils.generateToken(userDetails);
    }
}
