package com.RM.manageSystem.model.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * ClassName: RegisterRequest
 * PackageName: com.RM.manageSystem.model.dto.user
 * Description:
 *
 */
@Data
public class RegisterRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "用戶名", example = "AAA", required = true)
    private String userName;

    @Schema(description = "密碼", example = "1qaz2wsx", required = true)
    private String userPassword;

    @Schema(description = "確認密碼", example = "1qaz2wsx", required = true)
    private String checkPassword;
}
