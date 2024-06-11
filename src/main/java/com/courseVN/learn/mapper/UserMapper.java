package com.courseVN.learn.mapper;

import ch.qos.logback.core.model.ComponentModel;
import com.courseVN.learn.dto.request.UserCreationRequest;
import com.courseVN.learn.dto.request.UserUpdateRequest;
import com.courseVN.learn.dto.response.UserResponse;
import com.courseVN.learn.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring") // ta generate cai Mapper nay de su dung trong spring -> auto dependency injection
public interface UserMapper {
    User toUser(UserCreationRequest request);

    // co nhung case ta se muon map tung field cu the:
    // @Mapping(source="firstname", target="lastname"); -> firstname se lay gia tri cua lastname
    // @Mapping(target="firstname", ignore=true); -> no se ko mapping cai field firstname
  @Mapping(target = "roles", ignore = true)
    void updateUser(@MappingTarget User user, UserUpdateRequest userUpdateRequest); // map thk dto vao User
/*
*  vi la update user se mapping gia tri dau vao -> entity user nen role set<String> & set<Role> se bi loi
* => ta ignore field "roles"
* */


    // tao 1 mapper map tu User -> UserDtoResponseUser
    @Mapping(target = "password", ignore = true)
    UserResponse toUserResponse(User user);


}
