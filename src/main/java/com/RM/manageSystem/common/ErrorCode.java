package com.RM.manageSystem.common;

import lombok.Getter;

/**
 * 自定義錯誤碼
 */
@Getter
public enum ErrorCode {

    SUCCESS(20010, "操作成功", 200),
    PARAMS_ERROR(40000, "請求參數錯誤", 400),
    NOT_LOGIN_ERROR(40100, "未登入", 401),
    NO_AUTH_ERROR(40101, "無權限", 403),
    NOT_FOUND_ERROR(40400, "請求數據不存在", 404),
    FORBIDDEN_ERROR(40300, "禁止訪問", 403),
    SYSTEM_ERROR(50000, "系統內部異常", 500),
    OPERATION_ERROR(50001, "操作失敗", 500),
    TOKEN_EXPIRED(50002, "JWT token 失效", 401);

    private final int code;
    private final String message;
    private final int httpStatus;

    ErrorCode(int code, String message, int httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }


    /**
     * 根據 code 查找對應的 ErrorCode
     * @param code code
     * @return ErrorCode
     */
    public static ErrorCode fromCode(int code) {
        for (ErrorCode errorCode : values()) {
            if (errorCode.getCode() == code) {
                return errorCode;
            }
        }
        throw new IllegalArgumentException("Unknown code: " + code);
    }

}
