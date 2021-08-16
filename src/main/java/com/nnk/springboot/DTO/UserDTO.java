package com.nnk.springboot.DTO;

import com.nnk.springboot.config.validation.ValidPassword;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
public class UserDTO {

    private Integer id;

    @NotBlank(message = "{user.username.notBlank}")
    @Size(max = 125, message = "{user.username.size}")
    private String username;

    @NotBlank(message = "{user.password.notBlank}")
    @Size(max = 125, message = "{user.password.size}")
    @ValidPassword
    private String password;

    @NotBlank(message = "{user.fullname.notBlank}")
    @Size(max = 125, message = "{user.fullname.size}")
    private String fullname;

    @NotBlank(message = "{user.role.notBlank}")
    @Size(max = 125, message = "{user.role.size}")
    private String role;

    @Override
    public String toString() {
        return "UserDTO{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", fullname='" + fullname + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
