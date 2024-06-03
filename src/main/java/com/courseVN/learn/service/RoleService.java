package com.courseVN.learn.service;

import com.courseVN.learn.dto.request.RoleRequest;
import com.courseVN.learn.dto.response.RoleResponse;
import com.courseVN.learn.mapper.RoleMapper;
import com.courseVN.learn.repository.PermissionRepository;
import com.courseVN.learn.repository.RoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.HashSet;
import java.util.List;

@Service
@Slf4j
public class RoleService {
    @Autowired
    private RoleRepository _roleRepository;

    @Autowired
    private PermissionRepository _permissionRepository;

    @Autowired
    private RoleMapper _roleMapper;

    public RoleResponse create(RoleRequest request ){

        var role = _roleMapper.toRole(request);

        // sau khi map roi thi ta can lay cai list permission

        // lay ra cac cai permission ta da chuyen vao request
      var permissions =  _permissionRepository.findAllById(request.getPermissions());

      // chuyen tu list -> set
      role.setPermissions( new HashSet<>( permissions )); // create new HashSet<>() tu cai list permission.

       role = _roleRepository.save(role);

      return  _roleMapper.toRoleResponse( role );
    }

    public List<RoleResponse> getAll(){
        var roles = _roleRepository.findAll();
        return roles.stream().map( _roleMapper::toRoleResponse ).toList();
        // _roleMapper::toRoleResponse -> la chuyen du lieu trong Map vao luon
        // thay vi: a -> _roleMapper.toRoleResponse(a)
    }

    public void delete(String role){
        _roleRepository.deleteById(role);
    }

}
