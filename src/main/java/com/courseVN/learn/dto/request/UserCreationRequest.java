package com.courseVN.learn.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCreationRequest {
    @Size(min = 3, message = "Username password must be at least 8 characters")
    private String username;

    @Size(min = 8, message = "password must be at least 8 characters")
    private String password;
    private String firstName;
    private String lastName;
    private LocalDate dob;
}
