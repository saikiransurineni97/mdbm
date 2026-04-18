package com.project.mdbm.service;

import com.project.mdbm.dto.GenericAPIResponse;
import com.project.mdbm.entity.LoginRequest;
import com.project.mdbm.entity.User;
import com.project.mdbm.repository.UserRepository;
import com.project.mdbm.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public GenericAPIResponse saveUser(User user) {
        GenericAPIResponse response = new GenericAPIResponse();

        try {
            if (userRepository.findByEmail(user.getEmail()).isPresent()) {
                response.setCode(409);
                response.setMessage("User already exists with this email");
                return response;
            }

            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(user);

            response.setCode(200);
            response.setMessage("User registered successfully");
            return response;

        } catch (Exception e) {
            response.setCode(500);
            response.setMessage("Failed to register user: " + e.getMessage());
            return response;
        }
    }

    public User getUserByEmail(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        return userOpt.orElse(null);
    }

    public User updateUserByEmail(String email, User updatedUser) {
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isPresent()) {
            User existingUser = userOpt.get();

            existingUser.setFirstName(updatedUser.getFirstName());
            existingUser.setLastName(updatedUser.getLastName());
            existingUser.setPassword(updatedUser.getPassword());

            return userRepository.save(existingUser);
        }

        return null;
    }

    public GenericAPIResponse loginUser(LoginRequest loginRequest) {
        GenericAPIResponse response = new GenericAPIResponse();

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            String jwtToken = jwtService.generateToken(loginRequest.getEmail());

            response.setCode(200);
            response.setMessage("Login successful");
            response.setToken(jwtToken);
            return response;

        } catch (Exception e) {
            response.setCode(401);
            response.setMessage("Invalid email or password");
            return response;
        }
    }
}
