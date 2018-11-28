package com.bjpowernode.p2p.service.loan;

import com.bjpowernode.common.constant.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.Serializable;

/**
 * ClassName:OnlyNumberServiceImpl
 * Package:com.bjpowernode.p2p.service.loan
 * Description:
 * Date:2018/3/20 15:40
 * Author:13651027050
 */
@Service("onlyNumberServiceImpl")
public class OnlyNumberServiceImpl implements  OnlyNumberService{
    @Autowired
    private RedisTemplate<String,Serializable> redisTemplate;

    @Override
    public Long getOnlyNumber() {
        Long onlyNumber = redisTemplate.boundValueOps(Constants.ONLY_NUMBER).increment(1);
        return onlyNumber;
    }
}
