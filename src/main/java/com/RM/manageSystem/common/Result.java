package com.RM.manageSystem.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 響應結果封裝類
 */

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Result<T> {

    private int code;

    private String message;

    private Object data;

    public Result(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public Result(ErrorCode errorCode) {
        this.code = errorCode.getCode();
    }

    // 用於返回操作成功的結果(帶數據體)
    public static <T> Result<T> success(ErrorCode errorCode, String message, T data) {
        return new Result<>(errorCode.getCode(), message, data);
    }

    // 用於返回操作成功的結果(有數據體,使用通用 message)
    public static <T> Result<T> success(ErrorCode errorCode,T data) {
        return new Result<>(errorCode.getCode(), errorCode.getMessage(), data);
    }

    // 用於返回操作成功的結果(無數據體)
    public static <T> Result<T> success(ErrorCode errorCode, String message) {
        return new Result<>(errorCode.getCode(), message, null);
    }


    // 用於返回操作成功的結果(無數據體,使用通用 message)
    public static <T> Result<T> success(ErrorCode errorCode) {
        return new Result<>(errorCode.getCode(), errorCode.getMessage(), null);
    }


    // 用於返回操作失敗的結果
    public static <T> Result<T> error(ErrorCode errorCode, String message) {
        return new Result<>(errorCode.getCode(), message, null);
    }

    // 用於返回操作失敗的結果
    public static <T> Result<T> error(ErrorCode errorCode) {
        return new Result<>(errorCode);
    }

}



