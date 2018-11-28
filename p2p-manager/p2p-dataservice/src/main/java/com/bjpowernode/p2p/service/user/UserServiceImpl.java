package com.bjpowernode.p2p.service.user;

import com.bjpowernode.common.constant.Constants;
import com.bjpowernode.p2p.mapper.user.FinanceAccountMapper;
import com.bjpowernode.p2p.mapper.user.UserMapper;
import com.bjpowernode.p2p.model.user.FinanceAccount;
import com.bjpowernode.p2p.model.user.User;
import com.bjpowernode.p2p.model.vo.ResultObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * ClassName:UserServiceImpl
 * Package:com.bjpowernode.p2p.service.user
 * Description:
 * Date:2018/3/9 20:23
 * Author:13651027050
 */
@Service("userServiceImpl")
public class UserServiceImpl implements UserService {
    @Autowired
    private RedisTemplate<String,Serializable> redisTemplate;
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private FinanceAccountMapper financeAccountMapper;
    @Override
    public Long queryAllUserCount() {
        //从redis缓存中查询,有就使用,没有就在数据库中查询
        BoundValueOperations<String, Serializable> boundValueOps =  redisTemplate.boundValueOps(Constants.ALL_USER_COUNT);
        //从操作对象中获取对应的value值
        Long allUserCount = (Long) boundValueOps.get();

        if (null == allUserCount){
            //从数据库中查询数据
            allUserCount = userMapper.selectAllUserCount();
            //放到缓存中
            boundValueOps.set(allUserCount);
            //设置失效时间
            boundValueOps.expire(10, TimeUnit.MINUTES);
        }
        return allUserCount;
    }

    @Override
    public User queryUserByPhone(String phone) {
        return userMapper.selectUserByPhone(phone);
    }


    @Override
    public ResultObject register(User userInfo) {
        ResultObject resultObject = new ResultObject();
        resultObject.setErrorCode(Constants.SUCCESS);
        resultObject.setErrorMessage("注册成功!");
        //新增用户信息
        userInfo.setAddTime(new Date());
        userInfo.setLastLoginTime(new Date());

        int insertUserCount = userMapper.insertSelective(userInfo);

        if (insertUserCount > 0) {
            //获取当前用户的信息
            User user = userMapper.selectUserByPhone(userInfo.getPhone());

            //开立账户
            FinanceAccount financeAccount = new FinanceAccount();
            financeAccount.setUid(user.getId());
            financeAccount.setAvailableMoney(888.0);
            int insertFinanceCount = financeAccountMapper.insertSelective(financeAccount);

            if (insertFinanceCount <= 0){
                resultObject.setErrorCode(Constants.FAIL);
                resultObject.setErrorMessage("注册失败");
            }
        }else {
            //注册失败
            resultObject.setErrorCode(Constants.FAIL);
            resultObject.setErrorMessage("注册失败");
        }
        return resultObject;
    }

    @Override
    public int modifyUserByUserId(Map<String, Object> userMap) {

        User user = new User();
        user.setId((Integer) userMap.get("id"));
        user.setName((String) userMap.get("realName"));
        user.setIdCard((String) userMap.get("cardNo"));
        return userMapper.updateByPrimaryKeySelective(user);
    }

    @Override
    public User login(User user) {
        //查询用户是否存在
        User retUser = userMapper.selectUserByPhoneAndLoginPassword(user);

        if (null != retUser){
            //更新用户的最后登陆时间
            User updateUser = new User();
            updateUser.setId(user.getId());
            updateUser.setLastLoginTime(new Date());
            userMapper.updateByPrimaryKeySelective(updateUser);
        }
        return retUser;
    }
}
