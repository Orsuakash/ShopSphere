package com.ShopSphere.mapper;

import com.ShopSphere.DTOS.UserResponse;
import com.ShopSphere.Entity.User;
import com.ShopSphere.Entity.User;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;
@Component
public class UserMapper {

   public UserResponse toresponce(User user){
       return UserResponse.builder()
               .id(user.getId())
               .username(user.getUsername())
               .email(user.getEmail())
               .enabled(user.isEnabled())
               .roles(user.getRoles().stream
                       ().map(role -> role.getRoleName())
                       .collect(Collectors.toSet())).build();

   }


}
