package com.courseVN.learn.service;

import com.courseVN.learn.dto.request.UserCreationRequest;
import com.courseVN.learn.dto.request.UserUpdateRequest;
import com.courseVN.learn.dto.response.UserResponse;
import com.courseVN.learn.entity.User;
import com.courseVN.learn.exception.AppException;
import com.courseVN.learn.exception.ErrorCode;
import com.courseVN.learn.mapper.UserMapper;
import com.courseVN.learn.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
//@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
//RequireArgConstructor
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserMapper userMapper;

    public User createRequest(UserCreationRequest request){
        if( userRepository.existsByUsername(request.getUsername() ) ){
            throw new RuntimeException();
        }
        // runtimeException caught in exception.
//user.setId(Long.parseLong(UUID.randomUUID().toString()));

        // map giua UserDto vao User
        User user = userMapper.toUser(request);
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        user.setPassword( passwordEncoder.encode(request.getPassword()) );
        userRepository.save(user);
        return user;
    }

    public List<User> getUsers(){
        return userRepository.findAll();
    }

    public UserResponse getUserDetail(String userId){
       return userMapper.toUserResponse(userRepository.findById( userId ).orElseThrow( ()-> new AppException(ErrorCode.USER_NOTFOUND))) ;
    }

    public User getUserDetail2(String userId){
        return userRepository.findById( userId ).orElseThrow( ()-> new AppException(ErrorCode.USER_NOTFOUND)) ;
    }

    public UserResponse updateUser(String userId, UserUpdateRequest userUpdateRequest){
        User user = getUserDetail2(userId);
        userMapper.updateUser( user, userUpdateRequest );
        userRepository.save( user );
        return userMapper.toUserResponse(user);
    }

    public String deleteUser(String userId){
        User user = getUserDetail2(userId);
         userRepository.delete( user );
         return "delete successfully";
    }


}
