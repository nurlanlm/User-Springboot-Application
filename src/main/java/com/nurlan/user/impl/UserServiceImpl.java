package com.nurlan.user.impl;

import com.nurlan.user.dto.CreateUserRequest;
import com.nurlan.user.dto.UserDto;
import com.nurlan.user.entity.User;
import com.nurlan.user.exception.EmailExistException;
import com.nurlan.user.exception.UserNotFoundException;
import com.nurlan.user.repository.UserRepository;
import com.nurlan.user.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, ModelMapper modelMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
    }


    public UserDto login(String email, String password) {

        Optional<User> user = userRepository.findUserByEmail(email);

        if(user.isEmpty()) throw new UserNotFoundException("User not found. Email: " + email);
        if (passwordEncoder.matches(password, user.get().getPassword())) {
            return modelMapper.map(user, UserDto.class);
        }else {
            throw new RuntimeException("Wrong password");
        }

    }


    @Override
    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(user ->
                modelMapper.map(user, UserDto.class)
        ).collect(Collectors.toList());
    }

    @Override
    public UserDto createUser(CreateUserRequest createUserRequest) {
        User user = modelMapper.map(createUserRequest, User.class);

        Optional<User> optionalUser = userRepository.findUserByEmail(createUserRequest.getEmail());
        if(optionalUser.isPresent()) {
            throw new EmailExistException("This email \"" + createUserRequest.getEmail() + "\" already registered.");
        }

        user.setPassword(passwordEncoder.encode(createUserRequest.getPassword()));
        user.setVerified(false);
        user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found. ID: " + id));
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public UserDto updateUser(Long id, CreateUserRequest createUserRequest) {
        User user = modelMapper.map(getUserById(id), User.class);
        user.setId(user.getId());
        user.setName(createUserRequest.getName());
        user.setEmail(createUserRequest.getEmail());
        user.setAge(createUserRequest.getAge());
        user.setPassword(passwordEncoder.encode(createUserRequest.getPassword()));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public String deleteUserById(Long id) {
        User user = modelMapper.map(getUserById(id), User.class);
        userRepository.delete(user);
        return "User deleted successfully. ID: " + id;
    }

    @Override
    public String verifyUser(Long id, boolean verify) {
        User user = modelMapper.map(getUserById(id), User.class);
        user.setVerified(verify);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        return verify ? "User verified" : "User not verified";
    }

    @Override
    public UserDto getUserByEmail(String email) {
        Optional<User> optionalUser = Optional.ofNullable(userRepository.findUserByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found. Email: " + email)));
        return modelMapper.map(optionalUser.get(), UserDto.class);
    }

}