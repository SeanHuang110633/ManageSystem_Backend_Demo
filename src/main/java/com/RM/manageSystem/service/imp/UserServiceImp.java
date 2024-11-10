package com.RM.manageSystem.service.imp;


import com.RM.manageSystem.exception.BusinessException;
import com.RM.manageSystem.exception.SystemException;
import com.RM.manageSystem.mapper.UserMapper;
import com.RM.manageSystem.model.dto.user.UpdatePasswordRequest;
import com.RM.manageSystem.model.entity.User;
import com.RM.manageSystem.model.vo.UserVO;
import com.RM.manageSystem.service.UserService;
import com.RM.manageSystem.utils.JWTUtil;
import com.RM.manageSystem.utils.MD5Util;
import com.RM.manageSystem.utils.ThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static com.RM.manageSystem.common.ErrorCode.*;


/**
 * user service
 */
@Slf4j
@Service
public class UserServiceImp implements UserService {
    @Autowired
    private UserMapper userMapper;

    //使用Redis儲存JWT token校驗其有效性(排除使用者更換密碼但token還在效期內的問題)
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 根據用戶名查詢用戶資訊。
     *
     * @param username 用戶名
     * @return user 用戶對象
     */
    @Override
    public UserVO findUserByUsername(String username) {
        log.info("依使用者名稱查詢用戶資訊，使用者名稱: {}", username);
        User user = userMapper.findUserByUsername(username);
        if (user == null) {
            log.warn("找不到使用者名稱為 {} 的用戶", username);
            throw new BusinessException(NOT_FOUND_ERROR, "用戶不存在");
        }
        log.info("查詢用戶資訊成功，名稱: {}", username);
        UserVO userVO = convertToUserVO(user);
        return userVO;
    }

    /**
     * 註冊用戶
     *
     * @param username     用戶名稱
     * @param userPassword 用戶密碼
     */
    @Override
    public void register(String username, String userPassword) {
        log.info("註冊新用戶，用戶名稱: {}", username);

        // 檢查用戶名是否已存在
        if (userMapper.findUserByUsername(username) != null) {
            log.warn("註冊失敗，已存在相同使用者名稱: {}", username);
            throw new BusinessException(PARAMS_ERROR, "已存在相同使用者名稱");
        }

        String encryptedPassword = MD5Util.getMD5(userPassword);
        int registeredUserNum = userMapper.register(username, encryptedPassword);
        if (registeredUserNum > 0) {
            log.info("註冊新用戶成功，用戶名稱: {}", username);
            return;
        }
        throw new SystemException(SYSTEM_ERROR, "系統異常請聯繫管理員");
    }


    /**
     * 用戶登入
     *
     * @param username     用戶名稱
     * @param userPassword 用戶密碼
     * @return JWTToken
     */
    @Override
    public String login(String username, String userPassword) {
        UserVO loginUser = findUserByUsername(username);
        if (loginUser == null) {
            log.warn("登入失敗，用戶名稱錯誤或該用戶不存在: {}", username);
            throw new BusinessException(PARAMS_ERROR, "登入失敗，用戶名稱錯誤或該用戶不存在");
        }

        if (MD5Util.getMD5(userPassword).equals(loginUser.getUserPassword())) {
            HashMap<String, Object> userClaims = new HashMap<>();
            userClaims.put("id", loginUser.getId());
            userClaims.put("username", loginUser.getUsername());
            String userToken = JWTUtil.genToken(userClaims);
            ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
            operations.set("manageSys-userToken", userToken, 12, TimeUnit.HOURS);
            log.info("登入成功: {}", username);
            return userToken;
        }

        log.warn("登入失敗，密碼錯誤: {}", username);
        throw new BusinessException(PARAMS_ERROR, "登入失敗，密碼錯誤");
    }

    /**
     * 更新用戶資訊。
     *
     * @param user 用戶對象
     */
    @Transactional
    @Override
    public void updateUser(User user) {
        // 用戶名稱驗證
        Pattern userNamePattern = Pattern.compile("^[a-zA-Z0-9!@#$%^&*()_+\\u4e00-\\u9fa5]{2,20}$");
        boolean userNamePatternMatchResult = userNamePattern.matcher(user.getUsername()).matches();

        if (!userNamePatternMatchResult) {
            throw new BusinessException(PARAMS_ERROR, "用戶名稱不符合規定");
        }

        log.info("更新用戶資訊，用戶ID: {}", user.getId());

        if (!findUserById(user.getId())) {
            throw new BusinessException(NOT_FOUND_ERROR, "更新用戶不存在");
        }

        if (findUserByName(user.getUsername())) {
            throw new BusinessException(PARAMS_ERROR, "用戶名已被使用");
        }

        user.setUpdateTime(LocalDateTime.now());
        int updatedUserNum = userMapper.updateUser(user);
        if (updatedUserNum > 0) {
            log.info("更新用戶資訊成功，用戶ID: {}", user.getUsername());
            return;
        }
        throw new SystemException(SYSTEM_ERROR);
    }

    /**
     * 更新用戶密碼
     *
     * @param request 更新密碼請求
     * @param token   用戶JWT token
     */
    @Override
    @Transactional
    public void updatePassword(UpdatePasswordRequest request, String token) {
        log.info("service層更新用戶密碼");
        String oldPwd = request.getOldPwd();
        String newPwd = request.getNewPwd();
        String confirmedPwd = request.getConfirmedPwd();

        log.info("密碼更新參數: 舊密碼: {},新密碼: {},確認密碼: {}，用戶輸入不全", oldPwd, newPwd, confirmedPwd);
        // 驗證參數
        if (!StringUtils.hasLength(oldPwd) || !StringUtils.hasLength(newPwd) || !StringUtils.hasLength(confirmedPwd)) {
            log.warn("密碼更新失敗，用戶輸入不全");
            throw new BusinessException(PARAMS_ERROR, "所有欄位都必須輸入");
        }

        Pattern userPasswordPattern = Pattern.compile("^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z\\d!@#$%^&*()_+]{6,}$");
        boolean newPasswordMatchResult = userPasswordPattern.matcher(request.getNewPwd()).matches();

        if (!newPasswordMatchResult) {
            throw new BusinessException(PARAMS_ERROR, "密碼不符規定，必須是英文數字混合且長度大於6");
        }


        if (!newPwd.equals(confirmedPwd)) {
            log.warn("密碼更新失敗，新密碼與確認密碼不相符");
            throw new BusinessException(PARAMS_ERROR, "新密碼與確認密碼不相符");
        }

        //取得當前用戶資訊
        Map<String, Object> userClaims = ThreadLocalUtil.get();
        String username = (String) userClaims.get("username");
        UserVO loginUser = findUserByUsername(username);

        if (!loginUser.getUserPassword().equals(MD5Util.getMD5(oldPwd))) {
            log.warn("密碼更新失敗，舊密碼輸入錯誤: {}", username);
            throw new BusinessException(PARAMS_ERROR, "舊密碼輸入錯誤");
        }


        try {
            ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
            operations.getOperations().delete(token);
            log.info("清除使用者原有的token成功");
        } catch (Exception e) {
            throw new SystemException(SYSTEM_ERROR);
        }

        Integer id = loginUser.getId();
        int updatedPassword = userMapper.updatePassword(MD5Util.getMD5(newPwd), id);
        if (updatedPassword > 0) {
            log.info("密碼更新成功: {}", username);
            return;
        }

        log.error("更新用戶密碼失敗，使用者ID: {}", id);
        throw new SystemException(SYSTEM_ERROR);
    }


    /**
     * 查詢所有用戶列表。
     *
     * @return 用戶列表
     */
    @Override
    public List<UserVO> listUsers() {
        log.info("查詢所有用戶列表");
        List<User> users = userMapper.listUsers();

        if (users != null) {
            log.info("查詢所有用戶列表成功");
            // 使用 Stream 將 List<User> 轉換為 List<UserVO>
            return users.stream()
                    .map(this::convertToUserVO) // 使用 map 將每個 User 轉換為 UserVO
                    .toList();
        }

        log.info("查詢所有用戶列表失敗");
        throw new SystemException(SYSTEM_ERROR);
    }

    /**
     * 刪除用戶。
     *
     * @param id 用戶ID
     */
    @Override
    public void deleteUser(Integer id) {
        log.info("刪除用戶，使用者ID: {}", id);
        if (!findUserById(id)) {
            throw new BusinessException(NOT_FOUND_ERROR, "刪除對象不存在");
        }
        int deletedUserNum = userMapper.deleteUser(id);
        if (deletedUserNum > 0) {
            log.info("刪除用戶成功，使用者ID: {}", id);
            return;
        }
        log.error("刪除用戶失敗，使用者ID: {}", id);
        throw new SystemException(SYSTEM_ERROR);
    }


    /**
     * 根據Id判斷用戶是否存在
     *
     * @param id 用戶id
     * @return 存在與否
     */
    private boolean findUserById(Integer id) {
        String userName = userMapper.findUserById(id);
        return userName != null;
    }

    private boolean findUserByName(String name) {
        String userName = userMapper.findUserByName(name);
        return userName != null;
    }

    /**
     * User 到 UserVO 的轉換方法
     *
     * @param user user
     * @return UserVO
     */
    private UserVO convertToUserVO(User user) {
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }
}
