package com.leyou.auth.controller;

import com.leyou.auth.pojo.UserInfo;
import com.leyou.auth.service.AuthService;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.common.utils.CookieUtils;
import com.leyou.config.JwtProperties;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;

@Controller
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtProperties jwtProperties;

    @PostMapping("login")
    public ResponseEntity<Void> login(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            HttpServletRequest request, HttpServletResponse response) {
        // 调用service方法生成jwt
        String token = authService.login(username, password);

        if (StringUtils.isBlank(token)) {
            return ResponseEntity.badRequest().build();
        }
        // 使用cookieUtils.setCookie方法，就可以把jwt类型的token设置给cookie
        String cookieName = jwtProperties.getCookieName();
        int expire = jwtProperties.getExpire();
        CookieUtils.setCookie(request, response, cookieName, token, expire * 60);
//        CookieUtils.setCookie(request, response, jwtProperties.getCookieName(), token,
//                jwtProperties.getExpire(), "utf-8"); // 视频最后有true
        return ResponseEntity.ok().build();
    }

    @GetMapping("verify")
    public ResponseEntity<UserInfo> verify(@CookieValue("LY_TOKEN") String token,
                                           HttpServletRequest request, HttpServletResponse response) {
        try {

            // 使用公钥解析jwt获取用户信息
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtProperties.getPublicKey());
            // 为空，说明为正常使用
            if (userInfo == null) {
                System.out.println("userInfo 为空");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            // 刷新jwt过期时间
            token = JwtUtils.generateToken(userInfo, jwtProperties.getPrivateKey(), jwtProperties.getExpire());
            // 刷新cookie的过期时间
            CookieUtils.setCookie(request, response, jwtProperties.getCookieName(),
                    token, jwtProperties.getExpire() * 60, "utf-8");


            return ResponseEntity.ok(userInfo);
        } catch (Exception e) {
            System.out.println("异常！");
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

}
