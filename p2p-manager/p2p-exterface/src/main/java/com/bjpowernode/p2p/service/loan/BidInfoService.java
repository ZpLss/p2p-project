package com.bjpowernode.p2p.service.loan;

import com.bjpowernode.p2p.model.loan.BidInfo;
import com.bjpowernode.p2p.model.vo.PaginationVO;
import com.bjpowernode.p2p.model.vo.ResultObject;
import com.bjpowernode.p2p.model.vo.UserTop;

import java.util.List;
import java.util.Map;

/**
 * ClassName:BidInfoService
 * Package:com.bjpowernode.p2p.service.loan
 * Description:
 * Date:2018/3/9 20:46
 * Author:13651027050
 */
public interface BidInfoService {

    /**
     * 查询平台产品注册资金总额
     * @return
     */
    Double queryAllBidMoney();

    /**
     * 根据产品id查询产品投资信息
     * @param loanId
     * @return
     */
    List<BidInfo> queryBidInfoByLoanId(Integer loanId);


    /**
     * 投资业务
     * @param paramMap
     * @return
     */
    ResultObject invest(Map<String, Object> paramMap);

    /**
     * 获取用户投资排行榜
     * @return
     */
    List<UserTop> queryBidUserTop();

    /**
     * 根据用户标识获取查询用户最近投资记录
     * @param paramMap
     * @return
     */
    List<BidInfo> queryBidInfoTopByUid(Map<String, Object> paramMap);

    /**
     * 分页查询投资信息
     * @param paramMap
     * @return
     */
    PaginationVO<BidInfo> queryAllBidInfoByPage(Map<String, Object> paramMap);
}
