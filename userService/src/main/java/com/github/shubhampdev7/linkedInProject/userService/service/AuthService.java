package com.github.shubhampdev7.linkedInProject.userService.service;

import com.github.shubhampdev7.linkedInProject.userService.dto.LoginRequestDto;
import com.github.shubhampdev7.linkedInProject.userService.dto.SignupRequestDto;
import com.github.shubhampdev7.linkedInProject.userService.dto.UserDto;
import com.github.shubhampdev7.linkedInProject.userService.entity.User;
import com.github.shubhampdev7.linkedInProject.userService.exception.BadRequestException;
import com.github.shubhampdev7.linkedInProject.userService.exception.ResourceNotFoundException;
import com.github.shubhampdev7.linkedInProject.userService.repository.UserRepository;
import com.github.shubhampdev7.linkedInProject.userService.utils.BCrypt;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final JwtService jwtService;

    public UserDto signUp(SignupRequestDto signupRequestDto) {
        log.info("Signup a user with email: {}", signupRequestDto.getEmail());

        boolean exists = userRepository.existsByEmail(signupRequestDto.getEmail());
        if(exists){
            throw new BadRequestException("User with email already exists");
        }

        User user = modelMapper.map(signupRequestDto, User.class);
        user.setPassword(BCrypt.hash(signupRequestDto.getPassword()));

        user = userRepository.save(user);
        return modelMapper.map(user, UserDto.class);
    }

    public String login(LoginRequestDto loginRequestDto) {
        log.info("Login request for user with email: {}", loginRequestDto.getEmail());

        User user = userRepository.findByEmail(loginRequestDto.getEmail()).orElseThrow(() -> new BadRequestException("Incorrect email or password"));

        boolean isPasswordMatch = BCrypt.match(loginRequestDto.getPassword(), user.getPassword());

        if(!isPasswordMatch) {
            throw new BadRequestException("Incorrect email or password");
        }

        return jwtService.generateAccessToken(user);


    }
}
