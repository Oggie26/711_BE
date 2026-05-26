package com.example._1.service;

import com.example._1.dto.request.AuthRequest;
import com.example._1.dto.request.RegisterRequest;
import com.example._1.dto.response.AuthResponse;
import com.example._1.entity.Account;
import com.example._1.entity.User;
import com.example._1.enums.EnumRole;
import com.example._1.enums.ErrorCode;
import com.example._1.exception.AppException;
import com.example._1.repository.AccountRepository;
import com.example._1.repository.UserRepository;
import com.example._1.security.JwtService;
import com.example._1.service.interfaces.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

        private final AccountRepository accountRepository;
        private final PasswordEncoder passwordEncoder;
        private final JwtService jwtService;
        private final UserRepository userRepository;
        private final AuthenticationManager authenticationManager;

        @Override
        public AuthResponse login(AuthRequest request) {

                authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(
                                                request.getEmail(),
                                                request.getPassword()));

                Account account = accountRepository
                                .findByEmailAndIsDeletedFalse(request.getEmail())
                                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND_USER));

                Map<String, Object> claims = new HashMap<>();

                claims.put("userId", account.getUser().getId());
                claims.put("role", account.getRole() != null ? account.getRole().name() : "USER");

                String accessToken = jwtService.generateToken(claims, account.getEmail());
                String refreshToken = jwtService.generateRefreshToken(account.getEmail());

                return AuthResponse.builder()
                                .accessToken(accessToken)
                                .refreshToken(refreshToken)
                                .build();
        }

        @Override
        @Transactional
        public AuthResponse register(RegisterRequest request) {

                boolean exists = accountRepository.existsByEmail(request.getEmail());

                if (exists) {
                        throw new AppException(ErrorCode.EMAIL_EXISTS);
                }

                Account account = Account.builder()
                                .email(request.getEmail())
                                .password(passwordEncoder.encode(request.getPassword()))
                                .role(EnumRole.USER)
                                .build();
                accountRepository.save(account);

                User user = User.builder()
                                .point(0)
                                .phone(request.getPhone())
                                .fullName(request.getFullName())
                                .birthday(request.getBirthday())
                                .account(account)
                                .gender(request.getGender())
                                .build();

                userRepository.save(user);

                Map<String, Object> claims = new HashMap<>();
                claims.put("userId", user.getId());
                claims.put("role", "USER");

                String accessToken = jwtService.generateToken(claims, account.getEmail());
                String refreshToken = jwtService.generateRefreshToken(account.getEmail());


                return AuthResponse.builder()
                                .accessToken(accessToken)
                                .refreshToken(refreshToken)
                                .build();
        }
}