package com.leyou.auth.test;

import com.leyou.auth.pojo.UserInfo;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.auth.utils.RsaUtils;

import org.junit.Before;
import org.junit.jupiter.api.Test;

import java.security.PrivateKey;
import java.security.PublicKey;

public class JwtTest {

    private static final String pubKeyPath = "F:\\课程\\专3\\tmp\\rsa\\rsa.pub";

    private static final String priKeyPath = "F:\\课程\\专3\\tmp\\rsa\\rsa.pri";

    private PublicKey publicKey;

    private PrivateKey privateKey;

    @Test
    public void testRsa() throws Exception {
        RsaUtils.generateKey(pubKeyPath, priKeyPath, "234");
    }

    @Before
    public void testGetRsa() throws Exception {
        this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
        this.privateKey = RsaUtils.getPrivateKey(priKeyPath);
    }

    @Test
    public void testGenerateToken() throws Exception {
        privateKey = RsaUtils.getPrivateKey(priKeyPath);
        // 生成token
        String token = JwtUtils.generateToken(new UserInfo(20L, "jack"), privateKey, 5);
        System.out.println("token = " + token);
    }

    @Test
    public void testParseToken() throws Exception {
        String token = "eyJhbGciOiJSUzI1NiJ9.eyJpZCI6MjAsInVzZXJuYW1lIjoiamFjayIsImV4cCI6MTU5MDM5NDQyN30.BnhhUinm7cBDdcYDGVPJy96uYlv20RlOfSOMWujZSF5bNulkE2vatCTGEC1yal9M2Zi6qebzqjUyYU5FreaXqTJPUQ9iwDkP529y8dMA2N_NkU3rh72DIpF7N46QAEhDCJcAVTtVF9c3-Tdqo5pFrZvZZ03qy1PhIXmzpZ1ClFk";

        publicKey = RsaUtils.getPublicKey(pubKeyPath);
        // 解析token
        UserInfo user = JwtUtils.getInfoFromToken(token, publicKey);
        System.out.println("id: " + user.getId());
        System.out.println("userName: " + user.getUsername());
    }
}