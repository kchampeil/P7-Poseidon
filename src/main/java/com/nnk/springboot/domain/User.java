package com.nnk.springboot.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * User Entity allows to register a User
 */
@Getter
@Setter
@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    //TOASK : mis en nullable false même si pas not null dans le script SQL
    @Column(name = "username", nullable = false, unique = true, length = 125)
    private String username;

    @Column(name = "password", nullable = false, length = 125)
    private String password;

    @Column(name = "fullname", length = 125)
    private String fullname;

    @Column(name = "role", nullable = false, length = 125)
    private String role;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + this.role));
        return grantedAuthorities;
    }

    /* TODO V2: Account expiration, lock, disabling
                and Credentials expiration not managed in this version => all true */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
