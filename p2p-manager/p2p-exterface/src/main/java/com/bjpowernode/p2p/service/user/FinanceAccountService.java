package com.bjpowernode.p2p.service.user;

import com.bjpowernode.p2p.model.user.FinanceAccount;

/**
 * ClassName:FinanceAccountService
 * Package:com.bjpowernode.p2p.service.user
 * Description:
 * Date:2018/3/12 16:08
 * Author:13651027050
 */
public interface FinanceAccountService {

    /**
     * 根据用户id查询用户账户金额
     * @param uid
     * @return
     */
    FinanceAccount queryMyFinanceAcountByUid(int uid);
}
