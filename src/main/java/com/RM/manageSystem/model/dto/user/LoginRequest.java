package com.RM.manageSystem.model.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * ClassName: LoginRequest
 * PackageName: com.RM.manageSystem.model.dto.user
 * Description:
 *
 * @Create: 2024/9/7-下午 12:25
 */
@Data
public class LoginRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "用戶名", example = "AAA", required = true)
    private String userName;

    @Schema(description = "密碼", example = "1qaz2wsx", required = true)
    private String userPassword;
}
