package com.tinqinacademy.authentication.api.operations.getuserdetails;

import com.tinqinacademy.authentication.api.base.OperationOutput;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class GetUserDetailsOutput implements OperationOutput {
    private String username;
    private String password;
    private Collection<String> authorities;
}
