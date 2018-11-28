package com.bjpowernode.p2p.service.loan;

import com.bjpowernode.p2p.model.loan.IncomeRecord;
import com.bjpowernode.p2p.model.vo.PaginationVO;

import java.util.List;
import java.util.Map; /**
 * ClassName:IncomeRecordService
 * Package:com.bjpowernode.p2p.service.loan
 * Description:
 * Date:2018/3/15 21:28
 * Author:13651027050
 */
public interface IncomeRecordService {
    /**
     * 根据用户标识获取最近的收益记录
     * @param paramMap
     * @return
     */
    List<IncomeRecord> queryIncomeRecordByUid(Map<String, Object> paramMap);

    /**
     * 根据用户标识分页查询用户收益记录
     * @param paramMap
     * @return
     */
    PaginationVO<IncomeRecord> queryIncomeRecordByPage(Map<String, Object> paramMap);

    /**
     * 生成收益计划
     */
    void generateIncomePlan();

    /**
     * 返回收益计划
     */
    void generateIncomeBack();
}
