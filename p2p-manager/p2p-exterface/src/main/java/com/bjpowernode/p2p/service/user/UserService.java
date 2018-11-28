package com.bjpowernode.p2p.service.user;

import com.bjpowernode.p2p.model.user.User;
import com.bjpowernode.p2p.model.vo.ResultObject;

import java.util.Map;

/**
 * ClassName:UserService
 * Package:com.bjpowernode.p2p.service.user
 * Description:
 * Date:2018/3/9 20:22
 * Author:13651027050
 */
public interface UserService {
    /**
     *
     * 查询注册用户总数
     * @return
     */
    Long queryAllUserCount();

    /**
     * 根据手机号查询用户
     * @param phone
     * @return
     */
    User queryUserByPhone(String phone);



    /**
     * 用户注册
     * @return
     */
    ResultObject register(User userInfo);


    /**
     * 更新用户信息  真实姓名和身份证号
     * @param userMap
     * @return
     */
    int modifyUserByUserId(Map<String, Object> userMap);

    /**
     * 用户登陆
     * @param user
     * @return
     */

    User login(User user);
}
