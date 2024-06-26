package com.courseVN.learn.service;

import com.courseVN.learn.dto.request.ProfileCreateRequest;
import com.courseVN.learn.dto.request.UserCreationRequest;
import com.courseVN.learn.dto.request.UserUpdateRequest;
import com.courseVN.learn.dto.response.RoleResponse;
import com.courseVN.learn.dto.response.UserResponse;
import com.courseVN.learn.entity.Role;
import com.courseVN.learn.entity.User;
import com.courseVN.learn.enums.Roles;
import com.courseVN.learn.exception.AppException;
import com.courseVN.learn.exception.ErrorCode;
import com.courseVN.learn.mapper.ProfileMapper;
import com.courseVN.learn.mapper.RoleMapper;
import com.courseVN.learn.mapper.UserMapper;
import com.courseVN.learn.repository.RoleRepository;
import com.courseVN.learn.repository.UserRepository;
import com.courseVN.learn.repository.httpclient.ProfileClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.*;

@Service
@Slf4j
//@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
//RequireArgConstructor
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleRepository _roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ProfileClient profileClient;

    @Autowired
    private ProfileMapper profileMapper;

    @Autowired
    private RoleMapper roleMapper;

    public UserResponse createRequest(UserCreationRequest request){
        if( userRepository.existsByUsername(request.getUsername() ) ){
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        // runtimeException caught in exception.
//user.setId(Long.parseLong(UUID.randomUUID().toString()));

        // map giua UserDto vao User
        User user = userMapper.toUser(request);
       // PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10); bo dong nay vi chung ta da tao bean roi
        user.setPassword( passwordEncoder.encode(request.getPassword()) );


        // dua vao role cua nguoi dung:
        // tim role xem co ton tai ko?
        Role role_name = _roleRepository.findById(Roles.USER.name()).orElseThrow( ()-> new RuntimeException("role not found") );

        HashSet<Role> roles = new HashSet<>();
        roles.add(role_name);
      //  user.setRoles(roles);
        user.setRoles( roles );

      User user1 =  userRepository.save(user);

        ProfileCreateRequest profileRequest = profileMapper.toProfileCreateRequest(request);
        profileRequest.setUserId(String.valueOf(user1.getId()));

        // de get duoc cai token tu cai request thi chung ta se can "RequestContextHolder"

        // CAC BUOC DE LAY HEADER TRONG SPRING:

//        ServletRequestAttributes servletRequestAttributes =
//                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
//
//        var authHeader = servletRequestAttributes.getRequest().getHeader("Authorization");
//
//        log.info("Header {}", authHeader);

        profileClient.createProfile( profileRequest); //authHeader,

        // xu ly bo password:

        // chuyen tu role -> role response

        Set<RoleResponse> roles1 = new HashSet<RoleResponse>();
        user1.getRoles().forEach( role -> {
            roles1.add( roleMapper.toRoleResponse(role) );
        } );
        UserResponse userResponse = userMapper.toUserResponse(user1);
        userResponse.setRoles(roles1);

        return userResponse;
    }

    //@PreAuthorize("hasRole('ADMIN')")  -> Vay thi bay h o day! Khi sd "hasRole()" thì ta se chi ap dung cho ROLE_ADMIN or ROLE_USER
    // TAI VI: no se check cai prefix ROLE_ de lay ra role cua nguoi dung!
    // PreAuthorize -> spring se tao ra 1 cai proxy ngay truoc cai ham nay. no se check role truoc
    @PreAuthorize("hasAuthority('CREATE_SELF')") // su dung "hasAuthority()" cho cac permission -> no se map chinh xac cai authority
    public List<UserResponse> getUsers(){

        // map la tra ve 1 gtri moi List<User> -> List<UserResponse>
        return userRepository.findAll().stream().map( user ->
            userMapper.toUserResponse(user)
         ).toList();
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

    @PreAuthorize("hasRole('USER')")
    public User getUserDetail2(String userId){
        return userRepository.findById( userId ).orElseThrow( ()-> new AppException(ErrorCode.USER_NOTFOUND)) ;
    }

    public UserResponse updateUser(String userId, UserUpdateRequest userUpdateRequest){
        User user = getUserDetail2(userId);
        userMapper.updateUser( user, userUpdateRequest );
        // update lai password
        user.setPassword(passwordEncoder.encode(userUpdateRequest.getPassword()));

        // chung ta can co 1 cai list role lay thong tin tu request
        var roles = _roleRepository.findAllById(userUpdateRequest.getRoles());

        // maping no vao object user: -> vi o day la set<Role> phai tao 1 instance implement Set
        user.setRoles( new HashSet<>( roles ) );


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
        // guu token qua header la no lay duoc token trong nay!
       var context = SecurityContextHolder.getContext();

       // ngoai username co nhiều câc cái khác nữa như username, roles ...
       // context.getAuthentication().getAuthorities()

       String name = context.getAuthentication().getName(); // get dc cai authentication object la cai user da dang nhap

        User user = userRepository.findByUsername(name).orElseThrow( () -> new AppException( ErrorCode.USER_NOTFOUND ) );

        return userMapper.toUserResponse(user);
    }


}
