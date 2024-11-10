package com.RM.manageSystem.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * ClassName: UserVO
 * PackageName: com.RM.manageSystem.model.vo
 * Description:
 *
 * @Create: 2024/9/7-下午 12:31
 */

@Data
public class UserVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String username;
    private String userPassword;
    private String email;
    // private Integer userRole;

}
