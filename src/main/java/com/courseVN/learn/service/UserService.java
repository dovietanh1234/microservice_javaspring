package com.courseVN.learn.service;

import com.courseVN.learn.dto.request.UserCreationRequest;
import com.courseVN.learn.dto.request.UserUpdateRequest;
import com.courseVN.learn.dto.response.UserResponse;
import com.courseVN.learn.entity.User;
import com.courseVN.learn.enums.Roles;
import com.courseVN.learn.exception.AppException;
import com.courseVN.learn.exception.ErrorCode;
import com.courseVN.learn.mapper.UserMapper;
import com.courseVN.learn.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
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

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User createRequest(UserCreationRequest request){
        if( userRepository.existsByUsername(request.getUsername() ) ){
            throw new RuntimeException();
        }
        // runtimeException caught in exception.
//user.setId(Long.parseLong(UUID.randomUUID().toString()));

        // map giua UserDto vao User
        User user = userMapper.toUser(request);
       // PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10); bo dong nay vi chung ta da tao bean roi
        user.setPassword( passwordEncoder.encode(request.getPassword()) );

        // dua vao role cua nguoi dung:
        HashSet<String> roles = new HashSet<>();
        roles.add(Roles.USER.name());
        user.setRoles(roles);


        userRepository.save(user);
        return user;
    }

    @PreAuthorize("hasRole('ADMIN')") // PreAuthorize -> spring se tao ra 1 cai proxy ngay truoc cai ham nay. no se check role truoc
    public List<User> getUsers(){
        return userRepository.findAll();
    }

    // ngoai preAuthorize() se co: "PostAuthorize" no cx co tac dung tuong tu PreAuthorize()
    // nhung no co the check id xem co phai la chinh minh ko:
    // id trong payload in token  == id trong param cua minh chuyen vao.

    //PostAuthorize() la sau khi method getUserDetail() duoc thuc hien xong thi no moi check "PostAuthorize()" xem
    // co phai la dung role hay ko? con "@PreAuthorize" la no se check truoc.
    // trong thuc te thi PreAuthorize() duoc su dung nhieu hon PostAuthorize()
    // *Annotation PostAuthorize() no cung cap cho chung ta Spring express language de chung ta dua ra cai chi dan.
    @PostAuthorize("returnObject.username == authentication.name") // user chi co the lay dc thong tin cua chinh minh ma thoi
    // returnObject chinh la "UserResponse" ma chung ta tra ve == authentication.name cua spring security context holder
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

    // guu token len va lay ve thong tin cua minh:
    public UserResponse getMyInfo(){
        // sau khi dang nhap thanh cong user se duoc mapping vao Security Context holder qua Authentication
       var context = SecurityContextHolder.getContext();

       // ngoai username co nhiều câc cái khác nữa như username, roles ...
       // context.getAuthentication().getAuthorities()

       String name = context.getAuthentication().getName(); // get dc cai authentication object la cai user da dang nhap

        User user = userRepository.findByUsername(name).orElseThrow( () -> new AppException( ErrorCode.USER_NOTFOUND ) );

        return userMapper.toUserResponse(user);
    }


}
