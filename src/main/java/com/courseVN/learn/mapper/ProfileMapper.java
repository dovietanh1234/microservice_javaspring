package com.courseVN.learn.mapper;

import com.courseVN.learn.dto.request.ProfileCreateRequest;
import com.courseVN.learn.dto.request.UserCreationRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProfileMapper {
    ProfileCreateRequest toProfileCreateRequest(UserCreationRequest request);
}
