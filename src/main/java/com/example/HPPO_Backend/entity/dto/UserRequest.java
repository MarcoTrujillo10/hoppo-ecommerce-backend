package com.example.HPPO_Backend.entity.dto;

import com.example.HPPO_Backend.entity.Role;
import lombok.Data;

@Data
public class UserRequest {
    private String username;
    private String password;
    private String email;
    private Role role;
    private String name;
    private String lastName;
}
