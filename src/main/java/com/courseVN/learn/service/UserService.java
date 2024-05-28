package com.courseVN.learn.service;

import com.courseVN.learn.dto.request.UserCreationRequest;
import com.courseVN.learn.dto.request.UserUpdateRequest;
import com.courseVN.learn.entity.User;
import com.courseVN.learn.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private UserRepository userRepository;

    public User createRequest(UserCreationRequest request){
        User user = new User();
        user.setUsername(request.getUsername() );
        user.setFirstName(request.getFirstName() );
        user.setLastName(request.getLastName() );
        user.setPassword(request.getPassword() );
        user.setDob( request.getDob() );
        userRepository.save(user);
        return user;
    }

    public List<User> getUsers(){
        return userRepository.findAll();
    }

    public User getUserDetail(String userId){
       return userRepository.findById( userId ).orElseThrow( ()-> new RuntimeException("data not found") );
    }

    public User updateUser(String userId, UserUpdateRequest userUpdateRequest){
        User user = getUserDetail(userId);
        user.setPassword( userUpdateRequest.getPassword() );
        user.setLastName( userUpdateRequest.getLastName() );
        user.setFirstName( userUpdateRequest.getFirstName() );
        user.setDob( userUpdateRequest.getDob() );
        userRepository.save( user );
        return user;
    }

    public String deleteUser(String userId){
        User user = getUserDetail(userId);
         userRepository.delete( user );
         return "delete successfully";
    }


}
