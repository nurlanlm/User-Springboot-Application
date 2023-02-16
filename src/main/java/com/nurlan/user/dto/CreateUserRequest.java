package com.nurlan.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateUserRequest {

    private Long id;
    private String name;
    private int age;
    private String email;
    private String password;

}
