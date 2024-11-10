package com.RM.manageSystem.controller;

import com.RM.manageSystem.common.Result;
import com.RM.manageSystem.exception.BusinessException;
import com.RM.manageSystem.model.dto.user.LoginRequest;
import com.RM.manageSystem.model.dto.user.RegisterRequest;
import com.RM.manageSystem.model.dto.user.UpdatePasswordRequest;
import com.RM.manageSystem.model.dto.user.UpdateRequest;
import com.RM.manageSystem.model.entity.User;
import com.RM.manageSystem.model.vo.UserVO;
import com.RM.manageSystem.service.UserService;
import com.RM.manageSystem.utils.ThreadLocalUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static com.RM.manageSystem.common.ErrorCode.PARAMS_ERROR;
import static com.RM.manageSystem.common.ErrorCode.SUCCESS;

/**
 * user controller
 */

@Slf4j
@RestController
@RequestMapping("/user")
@Validated
@Tag(name = "User Management", description = "系統用戶API測試")
public class UserController {

    private final UserService userService;

    // 使用構造函數注入
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // region Register && Login

    /**
     * 註冊用戶
     *
     * @param request 包含註冊資訊的請求體
     * @return ResponseEntity<Result<?>> 註冊操作的結果
     */
    @Operation(summary = "註冊用戶",
            description = "創建一個新的用戶帳戶。用戶名長度要介於3-20個中英數字及特殊字符，密碼長度要大於等於6且必須包含英文及數字。")
    @PostMapping("/register")
    public ResponseEntity<Result<?>> register(@RequestBody RegisterRequest request) {
        if (request == null) {
            throw new BusinessException(PARAMS_ERROR);
        }
        log.info("開始註冊用戶: {}", request);
        // 驗證參數
        Pattern userNamePattern = Pattern.compile("^[a-zA-Z0-9!@#$%^&*()_+\\u4e00-\\u9fa5]{2,20}$");
        Pattern userPasswordPattern = Pattern.compile("^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z\\d!@#$%^&*()_+]{6,}$");
        boolean userNameMatchResult = userNamePattern.matcher(request.getUserName()).matches();
        boolean userPasswordMatchResult = userPasswordPattern.matcher(request.getUserPassword()).matches();
        if (!userNameMatchResult) {
            throw new BusinessException(PARAMS_ERROR, "帳號不符規定");
        }
        if (!userPasswordMatchResult) {
            throw new BusinessException(PARAMS_ERROR, "密碼不符規定");
        }
        if (!request.getUserPassword().equals(request.getCheckPassword())) {
            throw new BusinessException(PARAMS_ERROR, "兩次密碼輸入不相符");
        }
        userService.register(request.getUserName(), request.getUserPassword());
        log.info("用戶註冊成功");
        return new ResponseEntity<>(Result.success(SUCCESS, "註冊成功"), HttpStatus.CREATED);
    }

    /**
     * 登入用戶
     *
     * @param request 包含登入資訊的請求體
     * @return ResponseEntity<Result<?>> 登入操作的結果
     */
    @Operation(summary = "用戶登入",
            description = "驗證用戶名稱及密碼並返回訪問令牌。")
    @PostMapping("/login")
    public ResponseEntity<Result<String>> login(@RequestBody LoginRequest request) {
        if (request == null) {
            throw new BusinessException(PARAMS_ERROR);
        }
        log.info("開始用戶登入: {}", request);
        // 驗證參數
        Pattern userNamePattern = Pattern.compile("^[a-zA-Z0-9!@#$%^&*()_+\\u4e00-\\u9fa5]{2,20}$");
        Pattern userPasswordPattern = Pattern.compile("^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z\\d!@#$%^&*()_+]{6,}$");
        boolean userNameMatchResult = userNamePattern.matcher(request.getUserName()).matches();
        boolean userPasswordMatchResult = userPasswordPattern.matcher(request.getUserPassword()).matches();
        if (!userNameMatchResult) {
            throw new BusinessException(PARAMS_ERROR, "帳號不符規定");
        }
        if (!userPasswordMatchResult) {
            throw new BusinessException(PARAMS_ERROR, "密碼不符規定");
        }
        String token = userService.login(request.getUserName(), request.getUserPassword());
        return new ResponseEntity<>(Result.success(SUCCESS, "登入成功", token), HttpStatus.OK);
    }

    // endregion

    /**
     * 獲取當前用戶資訊
     *
     * @return ResponseEntity<Result<UserVO>> 包含用戶資訊的響應實體
     */
    @Operation(summary = "獲取當前用戶資訊", description = "根據當前登錄用戶的 JWT Token 獲取用戶詳細資訊")

    @GetMapping("/userinfo")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Result<UserVO>> getUserInfo() {
        log.info("查詢用戶資訊");
        Map<String, Object> userClaims = ThreadLocalUtil.get();
        String username = (String) userClaims.get("username");
        UserVO user = userService.findUserByUsername(username);
        return ResponseEntity.ok(Result.success(SUCCESS, user));
    }

    /**
     * 更新用戶資訊
     *
     * @param request 包含更新資訊的請求體
     * @return ResponseEntity<Result<?>> 更新操作的結果
     */
    @Operation(summary = "更新用戶資訊", description = "更新指定用戶的資訊")
    @PutMapping
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Result<?>> updateUser(@RequestBody @Validated UpdateRequest request) {
        if (request == null || request.getUsername().isBlank()) {
            throw new BusinessException(PARAMS_ERROR, "用戶名稱不得為空白");
        }
        // 沒修改:更改名稱與當前用戶同名
        Map<String, Object> userClaims = ThreadLocalUtil.get();
        String loginUsername = (String) userClaims.get("username");
        if(request.getUsername().equals(loginUsername)){
            // 這個wehelp的不改Email，先這樣
            throw new BusinessException(PARAMS_ERROR,"兄弟，你沒修改名字啊");
        }

        log.info("更新用戶資訊: {}", request.getUsername());
        User user = new User();
        BeanUtils.copyProperties(request, user);
        userService.updateUser(user);
        log.info("用戶資訊更新成功: {}", user.getUsername());
        return ResponseEntity.ok(Result.success(SUCCESS));
    }

    /**
     * 更新用戶密碼
     *
     * @param request 包含新密碼的請求體
     * @param token   用戶的 JWT Token
     * @return ResponseEntity<Result<?>> 更新密碼操作的結果
     */
    @Operation(summary = "更新用戶密碼", description = "更新當前登錄用戶的密碼")
    @PatchMapping("/updatePwd")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Result<?>> updatePassword(@RequestBody UpdatePasswordRequest request,
                                                    @RequestHeader("Authorization") String token) {
        if (request == null) {
            throw new BusinessException(PARAMS_ERROR);
        }
        log.info("接收更新密碼請求");
        userService.updatePassword(request, token);
        log.info("更新用戶密碼成功");
        return ResponseEntity.ok(Result.success(SUCCESS));
    }

    /**
     * 獲取所有用戶列表
     *
     * @return ResponseEntity<Result<List<UserVO>>> 包含所有用戶資訊的列表
     */
    @Operation(summary = "獲取所有用戶列表", description = "獲取系統中所有用戶的列表")
    @GetMapping
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Result<List<UserVO>>> listUsers() {
        log.info("查詢所有用戶列表");
        List<UserVO> users = userService.listUsers();
        log.info("查詢所有用戶列表成功");
        return ResponseEntity.ok(Result.success(SUCCESS, users));
    }

    /**
     * 刪除指定用戶（邏輯刪除）
     *
     * @param id 要刪除的用戶 ID
     * @return ResponseEntity<Result<?>> 刪除操作的結果
     */
    @Operation(summary = "刪除用戶", description = "邏輯刪除指定 ID 的用戶")
    @DeleteMapping
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Result<?>> deleteUser(@RequestParam Integer id) {
        log.info("刪除用戶: 用戶ID={}", id);
        userService.deleteUser(id);
        log.info("用戶刪除成功: 用戶ID={}", id);
        return ResponseEntity.ok(Result.success(SUCCESS));
    }
}
