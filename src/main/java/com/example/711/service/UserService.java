package com.example._1.service;

import com.example._1.dto.request.UserRequest;
import com.example._1.dto.request.UserUpdateRequest;
import com.example._1.dto.response.UserResponse;
import com.example._1.entity.Account;
import com.example._1.entity.UserProfile;
import com.example._1.repository.AccountRepository;
import com.example._1.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final AccountRepository accountRepository;
    private final UserProfileRepository userProfileRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse createUser(UserRequest request) {
        if (accountRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        Account account = Account.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();

        UserProfile profile = UserProfile.builder()
                .account(account)
                .loyaltyPoints(0)
                .build();

        account.setUserProfile(profile);
        account = accountRepository.save(account);
        
        return mapToResponse(account);
    }

    @Transactional
    public UserResponse updateUser(String id, UserUpdateRequest request) {
        Account account = accountRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            account.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        if (request.getRole() != null) {
            account.setRole(request.getRole());
        }

        account = accountRepository.save(account);
        return mapToResponse(account);
    }

    private UserResponse mapToResponse(Account account) {
        UserProfile profile = account.getUserProfile();
        return UserResponse.builder()
                .id(account.getId().toString())
                .username(account.getUsername())
                .role(account.getRole())
                .fullName(profile != null ? profile.getFullName() : null)
                .phoneNumber(profile != null ? profile.getPhoneNumber() : null)
                .address(profile != null ? profile.getAddress() : null)
                .loyaltyPoints(profile != null ? profile.getLoyaltyPoints() : 0)
                .build();
    }
}
