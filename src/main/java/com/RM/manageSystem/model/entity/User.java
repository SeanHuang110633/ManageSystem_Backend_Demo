package com.RM.manageSystem.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    private Integer id;
    @NotNull
    private String username;
    @JsonIgnore
    private String userPassword;
    @NotNull
    @Email
    private String email;
    // private Integer userRole; //系統使用者權限(1 for boss , 2 for employee)
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer isDelete;

    public static User UserBuilder;
}
