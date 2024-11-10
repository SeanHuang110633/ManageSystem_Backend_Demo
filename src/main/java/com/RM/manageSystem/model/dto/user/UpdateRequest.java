package com.RM.manageSystem.model.dto.user;

import lombok.Data;
import jakarta.validation.constraints.Email;

import java.io.Serializable;

/**
 * ClassName: UpdateRequest
 * PackageName: com.RM.manageSystem.model.dto.user
 * Description:
 *
 * @Create: 2024/9/7-下午 12:43
 */
@Data
public class UpdateRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String username;
    @Email
    private String email;
    // private Integer userRole;

}
