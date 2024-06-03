package com.courseVN.learn.mapper;

import com.courseVN.learn.dto.request.RoleRequest;
import com.courseVN.learn.dto.response.RoleResponse;
import com.courseVN.learn.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    /*
    * lưu ý: khi convert request ve role -> RoleRequest nhan vao 1 Set<String> Permission
    * nhưng trong Role entity nó là một Set<Permission> -> rieng cai nay phai ignore
    * */
    @Mapping( target = "permissions", ignore = true)
    Role toRole(RoleRequest request);
    RoleResponse toRoleResponse(Role role);
}
