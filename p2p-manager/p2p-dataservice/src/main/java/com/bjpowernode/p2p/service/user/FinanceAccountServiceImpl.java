package com.bjpowernode.p2p.service.user;

import com.bjpowernode.p2p.mapper.user.FinanceAccountMapper;
import com.bjpowernode.p2p.model.user.FinanceAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * ClassName:FinanceAccountServiceImpl
 * Package:com.bjpowernode.p2p.service.user
 * Description:
 * Date:2018/3/12 16:09
 * Author:13651027050
 */
@Service("financeAccountServiceImpl")
public class FinanceAccountServiceImpl implements FinanceAccountService {

    @Autowired
    private FinanceAccountMapper financeAccountMapper;
    @Override
    public FinanceAccount queryMyFinanceAcountByUid(int uid) {

        FinanceAccount financeAccount = financeAccountMapper.selectMyFinanceAcountByUid(uid);
        return financeAccount;
    }
}
