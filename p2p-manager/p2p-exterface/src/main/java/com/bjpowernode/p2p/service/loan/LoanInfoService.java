package com.bjpowernode.p2p.service.loan;

import com.bjpowernode.p2p.model.loan.LoanInfo;
import com.bjpowernode.p2p.model.vo.PaginationVO;

import java.util.List;
import java.util.Map;
/**
 * ClassName:LoanInfoService
 * Package:com.bjpowernode.p2p.service.loan
 * Description:
 * Date:2018/3/8 22:24
 * Author:13651027050
 */
public interface LoanInfoService {
    /**
    *获取平台产品历史平均年化收益利率
    */
    Double queryHistoryAverageRate();

    /**
     * 根据产品类型获取产品列表
     * @param paramMap
     * @return
     */

    List<LoanInfo> queryLoanInfoTopByProductType(Map<String, Object> paramMap);



    /**
     * 分页查询产品信息列表
     * @return
     */
    PaginationVO<LoanInfo> queryLoanInfoByPage(Map<String, Object> paramMap);



    /**
     * 根据产品id查询产品信息
     * @param loanId
     */
    LoanInfo queryLoanInfoByLoanId(Integer loanId);


}
