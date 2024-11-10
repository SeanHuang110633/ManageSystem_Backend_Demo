package com.RM.manageSystem.interceptors;

import com.RM.manageSystem.config.SwaggerConfig;
import com.RM.manageSystem.exception.BusinessException;
import com.RM.manageSystem.utils.JWTUtil;
import com.RM.manageSystem.utils.ThreadLocalUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

import static com.RM.manageSystem.common.ErrorCode.FORBIDDEN_ERROR;
import static com.RM.manageSystem.common.ErrorCode.TOKEN_EXPIRED;


@Slf4j
@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private SwaggerConfig swaggerConfig;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.debug("收到請求: {} {}", request.getMethod(), request.getRequestURI());

        // 允許 OPTIONS 請求通過
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            log.debug("允許 OPTIONS 請求通過");
            return true;
        }

        // 檢查是否是 Swagger 相關的請求
        if (swaggerConfig.isSwaggerRequest(request)) {
            // 允許 Swagger 相關請求通過
            return true;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String userToken = authHeader.substring(7); // Remove "Bearer " prefix
            // 驗證token
            try {
                log.info("前端傳來的userToken: {}", userToken);
                //從redis中取出相同token
                ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
                String serverToken = operations.get("manageSys-userToken");
                if (serverToken == null) {
                    log.error("redis token is expired ");
                    //token已失效
                    throw new BusinessException(TOKEN_EXPIRED, "請重新登入");
                }
                if (!serverToken.equals(userToken)) {
                    throw new BusinessException(FORBIDDEN_ERROR, "驗證失敗");
                }
                // 解析 JWT 令牌
                Map<String, Object> userClaims = JWTUtil.parseToken(userToken);
                // 把用戶數據存到threadLocal中
                ThreadLocalUtil.set(userClaims);
                // 驗證成功並放行
                return true;
            } catch (Exception e) {
                log.error("未授權的訪問，返回狀態碼401");
                // 驗證失敗，設置HTTP響應狀態為401未授權
                response.setStatus(401);
                // 拒絕放行
                return false;
            }
        }
        response.setStatus(401);
        return false;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //清空ThreadLocal中的數據
        ThreadLocalUtil.remove();
    }
}
