package com.example.gmall.user.service.impl;

import com.example.gmall.bean.UmsMember;
import com.example.gmall.bean.UmsMemberReceiveAddress;
import com.example.gmall.service.UserService;
import com.example.gmall.user.mapper.UmsMemberReceiveAddressMapper;
import com.example.gmall.user.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;
import com.alibaba.dubbo.config.annotation.Service;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserMapper userMapper;
    @Autowired
    UmsMemberReceiveAddressMapper umsMemberReceiveAddressMapper;


    @Override
    public List<UmsMember> getAllUser() {
        List<UmsMember> umsMembers =  userMapper.selectAll();
        return umsMembers;
    }

    @Override
    public List<UmsMemberReceiveAddress> getReceiveAddressByMemberId(String memberId) {


//        // 封装的参数对象
//        UmsMemberReceiveAddress umsMemberReceiveAddress = new UmsMemberReceiveAddress();
//        umsMemberReceiveAddress.setMemberId(memberId);
//        List<UmsMemberReceiveAddress> umsMemberReceiveAddresses = umsMemberReceiveAddressMapper.select(umsMemberReceiveAddress);

        //外键查询
        Example example = new Example(UmsMemberReceiveAddress.class);
        example.createCriteria().andEqualTo("memberId",memberId);
        List<UmsMemberReceiveAddress> umsMemberReceiveAddresses = umsMemberReceiveAddressMapper.selectByExample(example);

        return umsMemberReceiveAddresses;
    }
}
