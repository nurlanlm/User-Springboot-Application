package com.nurlan.user.service;

import com.nurlan.user.dto.CreateUserRequest;
import com.nurlan.user.dto.UserDto;

import java.util.List;

public interface UserService {

    List<UserDto> getAllUsers();
    UserDto createUser(CreateUserRequest createUserRequest);
    UserDto getUserById(Long id);
    UserDto updateUser(Long id, CreateUserRequest createUserRequest);
    String deleteUserById(Long id);
    String verifyUser(Long id, boolean verify);
    UserDto getUserByEmail(String email);
    UserDto login(String email, String password);
}
