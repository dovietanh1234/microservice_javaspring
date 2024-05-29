package com.courseVN.learn.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL) // khi serialize object nao null no se ko di kem vao dk tra ve
@Builder
public class ApiResponse <T> {
     int code = 1002;
     String message;
     T result;
}
