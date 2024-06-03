package com.courseVN.learn.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    @Id
    // auto create random string never duplicate
    //@GeneratedValue(strategy = GenerationType.UUID)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
     long id;
     String username;
     String firstName;
     String lastName;
     String password;
     LocalDate dob;

     //@ElementCollection // thang nay la de cho phep database luu vao 1 List nhe!

    // cach 1:
//    @Transient
//    Set<Role> roles = new HashSet<>();
    @ManyToMany
    Set<Role> roles;

}
