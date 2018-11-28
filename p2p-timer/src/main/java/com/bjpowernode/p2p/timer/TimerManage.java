package com.bjpowernode.p2p.timer;


import com.bjpowernode.p2p.service.loan.IncomeRecordService;
import com.bjpowernode.p2p.service.loan.RechargeRecordService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;



/**
 * ClassName:TimerManage
 * Package:com.bjpowernode.p2p.timer
 * Description:
 * Date:2018/3/16 10:41
 * Author:guoxin
 */
@Component
public class TimerManage {

    private Logger logger = LogManager.getLogger(TimerManage.class);

    @Autowired
    private IncomeRecordService incomeRecordService;
    @Autowired
    private RechargeRecordService rechargeRecordService;


    //@Scheduled(cron = "0/5 * * * * *")
    public void generateIncomePlan() {
        logger.info("----------收益生成开始-------------");
        incomeRecordService.generateIncomePlan();
        logger.info("----------收益生成结束-------------");
    }

    //@Scheduled(cron = "0/5 * * * * *")
    public void generateIncomeBack(){
        logger.info("----------收益返回开始-------------");
        incomeRecordService.generateIncomeBack();
        logger.info("----------收益返回结束-------------");

    }

    @Scheduled(cron = "0/5 * * * * *")
    public void dealRechargeRecord(){
        logger.info("------处理充值记录开始----");
        rechargeRecordService.dealRechargeRecord();
        logger.info("------处理充值记录结束----");

    }

}
