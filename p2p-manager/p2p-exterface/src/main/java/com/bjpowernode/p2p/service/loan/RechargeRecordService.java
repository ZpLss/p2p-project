package com.bjpowernode.p2p.service.loan;

import com.bjpowernode.p2p.model.loan.RechargeRecord;
import com.bjpowernode.p2p.model.vo.PaginationVO;

import java.util.List;
import java.util.Map; /**
 * ClassName:RechargeRecordService
 * Package:com.bjpowernode.p2p.service.loan
 * Description:
 * Date:2018/3/15 21:27
 * Author:13651027050
 */
public interface RechargeRecordService {

    /**
     * 根据用户标识查询充值记录
     * @param paramMap
     * @return
     */
    List<RechargeRecord> queryRechargeRecordByUid(Map<String, Object> paramMap);

    /**
     * 分页查询用户充值记录
     * @param paramMap
     * @return
     */
    PaginationVO<RechargeRecord> queryRechargeRecordByPage(Map<String, Object> paramMap);

    /**
     * 新增充值记录
     * @param rechargeRecord
     * @return
     */
    int addRechargeRecord(RechargeRecord rechargeRecord);

    /**
     * genju 更新充值记录状态2  充值失败
     * @return
     */
    int modifyRechargeRecordByRechargeNo(Map<String, Object> paramMap);

    /**
     * 用户充值
     * @param paramMap
     * @return
     */
    int recharge(Map<String, Object> paramMap);

    /**
     * 处理充值掉单
     */
    void dealRechargeRecord();
}
