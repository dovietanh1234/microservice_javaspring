package com.courseVN.learn.controller;


import com.courseVN.learn.dto.request.PermissionRequest;
import com.courseVN.learn.dto.request.RoleRequest;
import com.courseVN.learn.dto.response.ApiResponse;
import com.courseVN.learn.dto.response.PermissionResponse;
import com.courseVN.learn.dto.response.RoleResponse;
import com.courseVN.learn.service.PermissionService;
import com.courseVN.learn.service.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/role")
@Slf4j
public class RoleController {
    @Autowired
    private RoleService roleService;

    @PostMapping
    ApiResponse<RoleResponse> create(@RequestBody RoleRequest request){
        return ApiResponse.<RoleResponse>builder()
                .result(roleService.create(request))
                .build();
    }

    @GetMapping
    ApiResponse<List<RoleResponse>> getAll(){
        return ApiResponse.<List<RoleResponse>>builder()
                .result(roleService.getAll())
                .build();
    }

    @DeleteMapping("/{role}")
    ApiResponse<Void> delete(@PathVariable String role){
        roleService.delete(role);
        return ApiResponse.<Void>builder().build();
    }


}
