package com.courseVN.learn.dto.request;

import com.courseVN.learn.validator.DobConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateRequest {
     String password;
     String firstName;
     String lastName;
     @DobConstraint(min = 18, message = "INVALID_DOB")
     LocalDate dob;
     List<String> roles; // khi chuyen role vao cho user ta chi can mot list role string
}
