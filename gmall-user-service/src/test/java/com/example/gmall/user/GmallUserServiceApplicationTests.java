package com.example.gmall.user;


import com.example.gmall.bean.UmsMember;
import com.example.gmall.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GmallUserServiceApplicationTests {

    @Autowired
    UserService userService;


    @Test
    public void login(){
        UmsMember umsMember = new UmsMember();
        umsMember.setUsername("test");
        umsMember.setPassword("202cb962ac59075b964b07152d234b70");
        UmsMember login = userService.login(umsMember);
        System.out.println(login);
    }

}
