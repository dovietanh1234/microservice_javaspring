package com.courseVN.learn.service;

import com.courseVN.learn.dto.request.PermissionRequest;
import com.courseVN.learn.dto.response.PermissionResponse;
import com.courseVN.learn.entity.Permission;
import com.courseVN.learn.mapper.PermissionMapper;
import com.courseVN.learn.repository.PermissionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class PermissionService {

    @Autowired
   private PermissionRepository permissionRepository;

    @Autowired
    private PermissionMapper permissionMapper;
   public PermissionResponse create(PermissionRequest request){
        // using mapper to mapping data from request into entity Permission
        Permission permission = permissionMapper.toPermission(request);

        permission = permissionRepository.save(permission);

        return permissionMapper.toPermissionResponse(permission);
    }

   public List<PermissionResponse> getAll(){
        var permissions = permissionRepository.findAll();

     return permissions.stream().map(permissionMapper::toPermissionResponse).toList();
    }

   public void delete(String permission){
        permissionRepository.deleteById(permission);
    }

}
