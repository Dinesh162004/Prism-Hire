package com.prismhire.service;

import com.prismhire.dto.AuthResponse;
import com.prismhire.dto.LoginRequest;
import com.prismhire.dto.RegisterRequest;
import com.prismhire.entity.User;
import com.prismhire.repository.UserRepository;
import com.prismhire.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public AuthResponse register(RegisterRequest request) {
        if (request.getName() == null || request.getEmail() == null || request.getPassword() == null) {
            throw new IllegalArgumentException("All fields are required.");
        }

        if (userRepository.existsByEmail(request.getEmail().trim().toLowerCase())) {
            throw new IllegalStateException("User already exists.");
        }

        User user = new User();
        user.setName(request.getName().trim());
        user.setEmail(request.getEmail().trim().toLowerCase());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setProfilePicture("");
        user.setTheme(User.Theme.light);

        User savedUser = userRepository.save(user);
        String token = jwtUtil.generateToken(savedUser.getId(), savedUser.getName());

        return new AuthResponse(token, toUserDto(savedUser));
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail().trim().toLowerCase())
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials.");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getName());
        return new AuthResponse(token, toUserDto(user));
    }

    private AuthResponse.UserDto toUserDto(User user) {
        return new AuthResponse.UserDto(user.getId(), user.getName(), user.getEmail(), user.getProfilePicture());
    }
}
