package com.bjpowernode.p2p.service.user;

import com.alibaba.fastjson.JSONObject;
import com.bjpowernode.common.util.HttpClientTest;
import com.bjpowernode.p2p.mapper.loan.RechargeRecordMapper;
import com.bjpowernode.p2p.mapper.user.FinanceAccountMapper;
import com.bjpowernode.p2p.model.loan.RechargeRecord;
import com.bjpowernode.p2p.model.vo.PaginationVO;
import com.bjpowernode.p2p.service.loan.RechargeRecordService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ClassName:RechargeRecordServiceImpl
 * Package:com.bjpowernode.p2p.service.user
 * Description:
 * Date:2018/3/15 21:46
 * Author:13651027050
 */
@Service("rechargeRecordServiceImpl")
public class RechargeRecordServiceImpl implements RechargeRecordService{

    @Autowired
    private RechargeRecordMapper rechargeRecordMapper;
    @Autowired
    private FinanceAccountMapper financeAccountMapper;

    private Logger logger = LogManager.getLogger(RechargeRecordServiceImpl.class);


    @Override
    public List<RechargeRecord> queryRechargeRecordByUid(Map<String, Object> paramMap) {

        return rechargeRecordMapper.selectRechargeRecordByPage(paramMap);
    }

    @Override
    public PaginationVO<RechargeRecord> queryRechargeRecordByPage(Map<String, Object> paramMap) {

        PaginationVO<RechargeRecord> paginationVO = new PaginationVO<>();
        //分页查询充值记录
        List<RechargeRecord> rechargeRecordList =rechargeRecordMapper.selectRechargeRecordByPage(paramMap);

        //查询充值总条数
        Long totalRows = rechargeRecordMapper.selectTotalRows(paramMap);

        paginationVO.setDataList(rechargeRecordList);
        paginationVO.setTotal(totalRows);
        return paginationVO;
    }

    @Override
    public int addRechargeRecord(RechargeRecord rechargeRecord) {
        return rechargeRecordMapper.insertSelective(rechargeRecord);
    }

    @Override
    public int modifyRechargeRecordByRechargeNo(Map<String, Object> paramMap) {
        RechargeRecord rechargeRecord = new RechargeRecord();
        rechargeRecord.setRechargeNo((String) paramMap.get("out_trade_no"));
        rechargeRecord.setRechargeStatus((String) paramMap.get("rechargeStatus"));
        return rechargeRecordMapper.updateRechargeRecordByRechargeNo(rechargeRecord);
    }

    @Override
    public int recharge(Map<String, Object> paramMap) {
        //更新当前用户账户可用余额
        int updateFinanceCount = financeAccountMapper.updateFinanceAccountByRecharge(paramMap);
        if (updateFinanceCount > 0){
            //更新当前充值状态
            RechargeRecord rechargeRecord = new RechargeRecord();
            rechargeRecord.setRechargeNo((String) paramMap.get("out_trade_no"));
            rechargeRecord.setRechargeStatus((String) paramMap.get("rechargeStatus"));
            int retCont = rechargeRecordMapper.updateRechargeRecordByRechargeNo(rechargeRecord);
            if (retCont > 0){
                return 1;
            }
        }
        return 0;
    }

    @Override
    public void dealRechargeRecord() {
        //查询一小时之前的充值记录状态为0 的充值记录信息
        List<RechargeRecord> rechargeRecordList = rechargeRecordMapper.selectRechargeRecordByList("0");

        //循环遍历
        for (RechargeRecord rechargeRecord : rechargeRecordList){
            //获取到每条充值记录,查询该笔订单的状态
            Map<String,Object> paramMap = new HashMap<>();
            paramMap.put("out_trade_no",rechargeRecord.getRechargeNo());
            String result = HttpClientTest.doPost("http://localhost:9090/pay/api/alipayQuery",paramMap);

            //1.将json搁置的字符串转换为json对象
            JSONObject jsonObject = JSONObject.parseObject(result);
            //2.获取指定key:alipay_trade_query_response的值
            JSONObject tradeJson = jsonObject.getJSONObject("alipay_trade_query_response");

            String code = tradeJson.getString("code");
            //判断通信是否成功
            if ("10000".equals(code)){
                //4.获取trade_status,交易状态
                String trade_status = tradeJson.getString("trade_status");

                //判断充值交易是否成功
                /*交易状态：WAIT_BUYER_PAY（交易创建，等待买家付款）、
                TRADE_CLOSED（未付款交易超时关闭，或支付完成后全额退款）、
                TRADE_SUCCESS（交易支付成功）、
                TRADE_FINISHED（交易结束，不可退款）*/
                if ("TRADE_CLOSED".equals(trade_status)){
                    //更新状态
                    RechargeRecord updateRechargeRecord = new RechargeRecord();
                    updateRechargeRecord.setRechargeStatus("2");
                    updateRechargeRecord.setRechargeNo(rechargeRecord.getRechargeNo());

                    rechargeRecordMapper.updateRechargeRecordByRechargeNo(updateRechargeRecord);
                }

                if ("TRADE_SUCCESS".equals(trade_status) || "TRADE_FINISHED".equals(code)){
                    //充值成功,充值
                    paramMap.put("uid",rechargeRecord.getUid());
                    paramMap.put("rechargeMoney",rechargeRecord.getRechargeMoney());
                    int updateCount = financeAccountMapper.updateFinanceAccountByRecharge(paramMap);
                    if (updateCount>0){
                        //更改状态
                        RechargeRecord updateRecharge = new RechargeRecord();
                        updateRecharge.setRechargeNo(rechargeRecord.getRechargeNo());
                        updateRecharge.setRechargeStatus("1");
                        int updateRechargeCount = rechargeRecordMapper.updateRechargeRecordByRechargeNo(updateRecharge);
                        if (updateRechargeCount <= 0){
                            logger.info("充值订单号: " + rechargeRecord.getRechargeNo() + "充值失败");
                        }
                    }else {
                        logger.info("充值订单号: " + rechargeRecord.getRechargeNo() + "充值失败");
                    }
                }
            }else {
                logger.info("充值订单号: " + rechargeRecord.getRechargeNo() + "充值失败");
            }
        }

    }
}
