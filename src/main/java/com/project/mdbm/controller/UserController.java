package com.project.mdbm.controller;

import com.project.mdbm.dto.GenericAPIResponse;
import com.project.mdbm.entity.LoginRequest;
import com.project.mdbm.entity.User;
import com.project.mdbm.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/save")
    public ResponseEntity<GenericAPIResponse> saveUser(@Valid @RequestBody User user) {
        return ResponseEntity.ok(userService.saveUser(user));
    }

    @PostMapping("/login")
    public ResponseEntity<GenericAPIResponse> loginUser(@Valid @RequestBody LoginRequest loginRequest) {
        GenericAPIResponse genericAPIResponse = userService.loginUser(loginRequest);
        return ResponseEntity.ok(genericAPIResponse);
    }

    @PutMapping("/update")
    public User updateUserByEmail(@RequestParam String email,
                                  @RequestBody User user) {
        return userService.updateUserByEmail(email, user);
    }
}