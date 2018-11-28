package com.bjpowernode.p2p.service.loan;

import com.bjpowernode.common.util.DateUtils;
import com.bjpowernode.p2p.mapper.loan.BidInfoMapper;
import com.bjpowernode.p2p.mapper.loan.IncomeRecordMapper;
import com.bjpowernode.p2p.mapper.loan.LoanInfoMapper;
import com.bjpowernode.p2p.mapper.user.FinanceAccountMapper;
import com.bjpowernode.p2p.model.loan.BidInfo;
import com.bjpowernode.p2p.model.loan.IncomeRecord;
import com.bjpowernode.p2p.model.loan.LoanInfo;
import com.bjpowernode.p2p.model.vo.PaginationVO;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * ClassName:IncomeRecordServiceImpl
 * Package:com.bjpowernode.p2p.service.loan
 * Description:
 * Date:2018/3/15 21:57
 * Author:13651027050
 */
@Service("incomeRecordServiceImpl")
public class IncomeRecordServiceImpl implements IncomeRecordService {
    @Autowired
    private IncomeRecordMapper incomeRecordMapper;
    @Autowired
    private LoanInfoMapper loanInfoMapper;
    @Autowired
    private BidInfoMapper bidInfoMapper;
    @Autowired
    private FinanceAccountMapper financeAccountMapper;

    private Logger logger = LogManager.getLogger(IncomeRecordServiceImpl.class);

    @Override
    public List<IncomeRecord> queryIncomeRecordByUid(Map<String, Object> paramMap) {
        return incomeRecordMapper.selectIncomeRecordByPage(paramMap);
    }

    @Override
    public PaginationVO<IncomeRecord> queryIncomeRecordByPage(Map<String, Object> paramMap) {
        PaginationVO<IncomeRecord> paginationVO = new PaginationVO<>();

        //根据用户标识查询用户收益记录列表
        paginationVO.setDataList(incomeRecordMapper.selectIncomeRecordByPage(paramMap));
        paginationVO.setTotal(incomeRecordMapper.selectTotal(paramMap));

        return paginationVO;
    }

    @Override
    public void generateIncomePlan() {
        //查询产品状态为1的列表,  List<>
        List<LoanInfo> loanInfoList = loanInfoMapper.selectLoanInfoByProductStatus(1);

        // 循环遍历产品  每一个产品详情
        for (LoanInfo loanInfo:loanInfoList){
            //根据产品标识获取到该产品的所有投资记录  List<投资记录>
            List<BidInfo> bidInfoList = bidInfoMapper.selectBidInfoListByLoanId(loanInfo.getId());
            Integer productType = loanInfo.getProductType();
            Double rate = loanInfo.getRate();
            Integer cycle = loanInfo.getCycle();
            Date productFullTime = loanInfo.getProductFullTime();

            //循环遍历List<投资记录>  每一条投资记录
            for (BidInfo bidInfo:bidInfoList){
                //每条投资成功对应一条收益记录
                IncomeRecord incomeRecord = new IncomeRecord();
                incomeRecord.setUid(bidInfo.getUid());
                incomeRecord.setLoanId(bidInfo.getLoanId());
                incomeRecord.setBidId(bidInfo.getId());
                incomeRecord.setBidMoney(bidInfo.getBidMoney());
                incomeRecord.setIncomeStatus(0);

                Double bidMoney = bidInfo.getBidMoney();
                Date incomeDate = null;//收益时间
                Double incomeMoney = null;//收益金额

                incomeRecord.setIncomeDate(new Date());

                if (productType == 0){
                    //新手宝
                    incomeDate = DateUtils.getDateByAddDays(productFullTime,cycle);
                    incomeMoney = bidMoney * (rate / 100 / DateUtils.getDaysByYear(Calendar.getInstance().get(Calendar.YEAR)))* cycle;
                }else {
                    //优选和散标
                    incomeDate = DateUtils.getDateByAddMonths(productFullTime,cycle);
                    incomeMoney = bidMoney * (rate / 100 / DateUtils.getDaysByYear(Calendar.getInstance().get(Calendar.YEAR)))* DateUtils.getDistanceOfDate(productFullTime,incomeDate);
                }
                incomeMoney = Math.round(incomeMoney * Math.pow(10,2)) /Math.pow(10,2);

                incomeRecord.setIncomeMoney(incomeMoney);
                incomeRecord.setIncomeDate(incomeDate);

                //新增一条收益记录
                int insertIncomeRecordCount = incomeRecordMapper.insertSelective(incomeRecord);

                //
                if (insertIncomeRecordCount > 0){
                    //成功
                    logger.info("用户uid:" + bidInfo.getUid() +  bidInfo.getId() +  "成功");

                }else {
                    logger.info("用户uid:" + bidInfo.getUid() +  bidInfo.getId() + "失败");
                }
            }
            //更新产品状态为2满标,且生成收益计划添加满标时间
            LoanInfo updateLoanInfo = new LoanInfo();
            updateLoanInfo.setId(loanInfo.getId());
            updateLoanInfo.setProductStatus(2);
            int updateLoanInfoCount = loanInfoMapper.updateByPrimaryKeySelective(updateLoanInfo);
            if (updateLoanInfoCount > 0) {
                logger.info("更新产品标识为" + loanInfo.getId() + "更新为2成功");

            }else {
                logger.info("更新产品标识为" + loanInfo.getId() + "更新为2失败");

            }
        }
    }

    @Override
    public void generateIncomeBack() {
        //查询收益时间与当前时间相等的收益记录
        List<IncomeRecord> incomeRecordList = incomeRecordMapper.selectIncomeRecordByIncomeStatus(0);

        //循环遍历收益记录
        for (IncomeRecord incomeRecord: incomeRecordList){
            int uid = incomeRecord.getUid();
            Double bidMoney = incomeRecord.getBidMoney();
            Double incomeMoney = incomeRecord.getIncomeMoney();

            //将当前收益记录的投资金额和收益金额返还给当前的用户(更新当前用户的账户可用余额)
            Map<String,Object> paramMap = new HashMap<>();
            paramMap.put("uid",uid);
            paramMap.put("bidMoney",bidMoney);
            paramMap.put("incomeMoney",incomeMoney);

            int updateCount = financeAccountMapper.updateFinanceAccountByIncomeBack(paramMap);
            if (updateCount > 0){
                logger.info("收益返回成功");
                //将当前收益记录的状态更新为  1:已返还
                IncomeRecord updateIncomeRecord = new IncomeRecord();
                updateIncomeRecord.setId(incomeRecord.getId());
                updateIncomeRecord.setIncomeStatus(1);
                int updateIncomeCount = incomeRecordMapper.updateByPrimaryKeySelective(updateIncomeRecord);
                if (updateIncomeCount > 0){
                    logger.info("收益状态更新成功");
                }else {
                    logger.info("收益状态更新失败");
                }
            }else {
                logger.info("收益返回失败");
            }
        }
    }
}
