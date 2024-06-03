package com.courseVN.learn.mapper;

import com.courseVN.learn.dto.request.PermissionRequest;
import com.courseVN.learn.dto.request.UserCreationRequest;
import com.courseVN.learn.dto.response.PermissionResponse;
import com.courseVN.learn.entity.Permission;
import com.courseVN.learn.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPermission(PermissionRequest request);
    PermissionResponse toPermissionResponse(Permission permission);
}
