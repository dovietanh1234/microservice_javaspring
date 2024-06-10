package com.courseVN.learn.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProfileCreateRequest {
    String userId;
    String firstName;
    String lastName;
    LocalDate dob;
    String city;
}
