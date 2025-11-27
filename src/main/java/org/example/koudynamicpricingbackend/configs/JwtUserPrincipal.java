package org.example.koudynamicpricingbackend.configs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter
@AllArgsConstructor
public class JwtUserPrincipal {
    private String email;
    private Collection<? extends GrantedAuthority> authorities;
}
