package com.ShopSphere.DTOS;


import lombok.*;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class UserResponse {

    private Long id;

    private String username;

    private String email;

    private boolean enabled;

    private Set<String> roles;

}
