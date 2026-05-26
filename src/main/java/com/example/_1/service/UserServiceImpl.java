package com.example._1.service;

import com.example._1.dto.response.CategoryResponse;
import com.example._1.dto.response.PageResponse;
import com.example._1.dto.response.UserResponse;
import com.example._1.entity.Category;
import com.example._1.entity.User;
import com.example._1.enums.EnumStatus;
import com.example._1.enums.ErrorCode;
import com.example._1.exception.AppException;
import com.example._1.repository.UserRepository;
import com.example._1.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Unauthorized");
        }
        String email = authentication.getName();
        return userRepository.findByAccountEmail(email).orElseThrow(() ->
                new AppException(ErrorCode.USER_NOT_FOUND));
    }

    @Override
    public UserResponse getUserById(UUID id) {
        User user = userRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new  AppException(ErrorCode.USER_NOT_FOUND));
        return mapToUserResponse(user);
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .filter(user -> !user.isDeleted())
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PageResponse<UserResponse> searchUser(String request, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);
        Page<User> userPage = userRepository.searchByKeywordNative(request, pageable);

        List<UserResponse> data = userPage.getContent().stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());

        return new PageResponse<>(
                data,
                userPage.getNumber(),
                userPage.getSize(),
                userPage.getTotalElements(),
                userPage.getTotalPages()
        );
    }

    @Override
    public UserResponse profile() {
        return mapToUserResponse(getCurrentUser());
    }

    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getAccount().getEmail())
                .fullName(user.getFullName())
                .gender(user.getGender())
                .birthday(user.getBirthday())
                .phone(user.getPhone())
                .point(user.getPoint())
                .build();
    }
}
