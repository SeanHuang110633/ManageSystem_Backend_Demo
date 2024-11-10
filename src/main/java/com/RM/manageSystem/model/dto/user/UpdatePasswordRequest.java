package com.RM.manageSystem.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * ClassName: UpdatePasswordRequest
 * PackageName: com.RM.manageSystem.model.dto.user
 * Description:
 *
 * @Create: 2024/9/11-下午 07:52
 */

@Data
public class UpdatePasswordRequest implements Serializable{
    private static final long serialVersionUID = 1L;

    private String oldPwd;
    private String newPwd;
    private String confirmedPwd;

}
