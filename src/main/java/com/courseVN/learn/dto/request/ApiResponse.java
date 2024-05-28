package com.courseVN.learn.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL) // khi serialize object nao null no se ko di kem vao dk tra ve
public class ApiResponse <T> {
    private int code = 1002;
    private String message;
    private T result;
}
