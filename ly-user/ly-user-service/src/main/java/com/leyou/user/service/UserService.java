package com.leyou.user.service;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.NumberUtils;
import com.leyou.user.mapper.UserMapper;
import com.leyou.user.pojo.User;
import com.leyou.utils.CodecUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String key_prefix = "user:verify:phone";

    public Boolean checkData(String data, Integer type) {
        User record = new User();

        // 判断数据类型
        switch (type) {
            case 1:
                record.setUsername(data);
                break;
            case 2:
                record.setPhone(data);
                break;
            default:
                throw new LyException(ExceptionEnum.INVALID_USER_DATA_TYPE);
        }
        return userMapper.selectCount(record) == 0;
    }

    public void sendCode(String phone) {
        // 生成key
        String key = key_prefix + phone;

        // 生成验证码
        String code = NumberUtils.generateCode(6);
        Map<String, String> msg = new HashMap<>();
        msg.put("phone", phone);
        msg.put("code", code);
        // 发送验证码
        amqpTemplate.convertAndSend("ly.sms.exchange", "sms.verify.code", msg);

        // 保存验证码
        redisTemplate.opsForValue().set(key, code, 5, TimeUnit.MINUTES);
    }

    public void register(User user, String code) {
        // 校验验证码
        String redisCode = redisTemplate.opsForValue().get(key_prefix + user.getPhone());
//        if (!StringUtils.equals(code, redisCode)) {
//            System.out.println("验证码不对");
//            return;
//        }

        // 生成随机码
        String salt = CodecUtils.generateSalt();
        user.setSalt(salt);

        // 加盐加密存储MD5
        user.setPassword(CodecUtils.md5Hex(user.getPassword(), salt));

        // 新增用户信息
        user.setId(null);
        user.setCreated(new Date());
        userMapper.insertSelective(user);

        // 删除验证码
        redisTemplate.delete(key_prefix + user.getPhone());
    }

    public User queryUser(String username, String password) {

        // 1,先根据用户名查询用户
        User record = new User();
        record.setUsername(username);
        User user = userMapper.selectOne(record);

        if (user == null) {
            return user;
        }
        // 2 对用户输入的密码加盐加密
        password = CodecUtils.md5Hex(password, user.getSalt());

        // 3 判断用户输入的密码是否正确
        if (!StringUtils.equals(password, user.getPassword())) {
            return null;
        }

        return user;
    }
}
