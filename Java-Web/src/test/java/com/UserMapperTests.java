package com;

import com.bean.User;
import com.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

@SpringBootTest
public class UserMapperTests {
    @Autowired
    private UserMapper userMapper;

    @Test
    void contextLoads() {
        String rawPassword = "xz";
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(rawPassword);

        System.out.println("Encoded password: " + encodedPassword);
    }
}
