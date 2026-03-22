package com.nxh.redis.controller;

import com.nxh.redis.entity.User;
import com.nxh.redis.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public String getUser(@PathVariable Long id) {
        long start = System.currentTimeMillis();
        User user = userService.getUser(id);
        long end = System.currentTimeMillis();
        return user.getName() + " | Time: " + (end - start) + " ms";
    }
}