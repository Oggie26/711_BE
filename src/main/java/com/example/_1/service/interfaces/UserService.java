package com.example._1.service.interfaces;

import com.example._1.dto.response.PageResponse;
import com.example._1.dto.response.UserResponse;
import com.example._1.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    User getCurrentUser();
    UserResponse getUserById(UUID id);
    List<UserResponse> getAllUsers();
    PageResponse<UserResponse> searchUser(String request, int page, int size);
    UserResponse profile();
}
