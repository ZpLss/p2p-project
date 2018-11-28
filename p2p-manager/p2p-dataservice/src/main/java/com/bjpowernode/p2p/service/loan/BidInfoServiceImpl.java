package com.bjpowernode.p2p.service.loan;

import com.bjpowernode.common.constant.Constants;
import com.bjpowernode.p2p.mapper.loan.BidInfoMapper;
import com.bjpowernode.p2p.mapper.loan.LoanInfoMapper;
import com.bjpowernode.p2p.mapper.user.FinanceAccountMapper;
import com.bjpowernode.p2p.model.loan.BidInfo;
import com.bjpowernode.p2p.model.loan.LoanInfo;
import com.bjpowernode.p2p.model.vo.PaginationVO;
import com.bjpowernode.p2p.model.vo.ResultObject;
import com.bjpowernode.p2p.model.vo.UserTop;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * ClassName:BidInfoServiceImpl
 * Package:com.bjpowernode.p2p.service.loan
 * Description:
 * Date:2018/3/9 22:32
 * Author:13651027050
 */
@Service("bidInfoServiceImpl")
public class BidInfoServiceImpl implements BidInfoService{

    @Autowired
    private RedisTemplate<String,Serializable> redisTemplate;
    @Autowired
    private BidInfoMapper bidInfoMapper;
    @Autowired
    private LoanInfoMapper loanInfoMapper;
    @Autowired
    private FinanceAccountMapper financeAccountMapper;


    @Override
    public Double queryAllBidMoney() {
        //先从redis缓存中查看,有酒用,没有,就去数据库查询
        BoundValueOperations<String, Serializable> boundValueOps = redisTemplate.boundValueOps(Constants.ALL_BID_MONEY);
        //获取操作对象对应的value值
        Double allBidMoney = (Double) boundValueOps.get();

        if (null == allBidMoney){
            //从数据库查询
            allBidMoney = bidInfoMapper.selectAllBidMoney();
            //保存到redis缓存
            boundValueOps.set(allBidMoney);
            //设置失效时间
            boundValueOps.expire(10, TimeUnit.MINUTES);

        }
        return allBidMoney;
    }

    @Override
    public List<BidInfo> queryBidInfoByLoanId(Integer loanId) {

        List<BidInfo> bidInfoList = bidInfoMapper.selectBidInfoListByLoanId(loanId);
        return bidInfoList;
    }

    @Override
    public ResultObject invest(Map<String, Object> paramMap) {

        ResultObject resultObject = new ResultObject();
        //超卖:实际销售数量超过了计划的产品的数量

        //投资业务:更新产品可投金额,添加投资记录,更新账户
        //根据产品表示获取产品信息
        LoanInfo loanInfo = loanInfoMapper.selectByPrimaryKey((Integer) paramMap.get("loanId"));
        paramMap.put("version", loanInfo.getVersion());

        //更新产品可投金额
        int updateLoanInfoLeftProductMoneyCount = loanInfoMapper.updateLoanInfoLeftProductMoneyByLoanId(paramMap);

        if (updateLoanInfoLeftProductMoneyCount > 0){

            //添加投资记录
            BidInfo bidInfo = new BidInfo();
            bidInfo.setUid((Integer) paramMap.get("uid"));
            bidInfo.setBidMoney((Double) paramMap.get("bidMoney"));
            bidInfo.setLoanId((Integer) paramMap.get("loanId"));
            bidInfo.setBidTime(new Date());
            bidInfo.setBidStatus(1);

            int insertBidInfoCount = bidInfoMapper.insert(bidInfo);
            if (insertBidInfoCount > 0){
                //更新账户可用余额
                int updateAvailableMoneyCount = financeAccountMapper.updateAvailableMoneyByUid(paramMap);
                if (updateAvailableMoneyCount > 0){
                    //判断当前剩余可投金额为0
                    //查询产品的详情
                    LoanInfo loanInfo1 = loanInfoMapper.selectByPrimaryKey((Integer) paramMap.get("loanId"));
                    if (loanInfo1.getLeftProductMoney() == 0 ){
                        //更新当前产品的状态为1,已满标,添加满标时间
                        LoanInfo updateLoanInfo = new LoanInfo();
                        updateLoanInfo.setId((Integer) paramMap.get("loanId"));
                        updateLoanInfo.setProductStatus(1);
                        updateLoanInfo.setProductFullTime(new Date());
                        int updateLoanInfoCount = loanInfoMapper.updateByPrimaryKeySelective(updateLoanInfo);
                        if (updateLoanInfoCount <= 0) {
                            //投资失败
                            resultObject.setErrorMessage("投资失败");
                            resultObject.setErrorCode(Constants.FAIL);
                        }
                    }

                    //更新成功,将用户的投资记录存放到redis中
                    redisTemplate.opsForZSet().incrementScore(Constants.INVEST_TOP,(String)paramMap.get("phone"),(Double) paramMap.get("bidMoney"));
                    resultObject.setErrorMessage("投资成功");
                    resultObject.setErrorCode(Constants.SUCCESS);
                }else {
                    resultObject.setErrorMessage("投资失败");
                    resultObject.setErrorCode(Constants.FAIL);
                }
            }else {
                resultObject.setErrorMessage("投资失败");
                resultObject.setErrorCode(Constants.FAIL);
            }
        }else {
            resultObject.setErrorMessage("投资失败");
            resultObject.setErrorCode(Constants.FAIL);
        }
        return resultObject;
    }

    @Override
    public List<UserTop> queryBidUserTop() {

        List<UserTop> userTopList = new ArrayList<>();

        Set<ZSetOperations.TypedTuple<Serializable>> typedTuples = redisTemplate.opsForZSet().reverseRangeWithScores(Constants.INVEST_TOP, 0, 9);

        Iterator<ZSetOperations.TypedTuple<Serializable>> iterator = typedTuples.iterator();
        while (iterator.hasNext()){
            ZSetOperations.TypedTuple<Serializable> next = iterator.next();
            Double score = next.getScore();//用户累计投资金额
            String phone = (String) next.getValue();

            UserTop userTop = new UserTop();
            userTop.setPhone(phone);
            userTop.setScore(score);

            userTopList.add(userTop);

        }
        return userTopList;
    }

    @Override
    public List<BidInfo> queryBidInfoTopByUid(Map<String, Object> paramMap) {

        return bidInfoMapper.selectBidInfoByPage(paramMap);
    }

    @Override
    public PaginationVO<BidInfo> queryAllBidInfoByPage(Map<String, Object> paramMap) {
        PaginationVO<BidInfo> paginationVO = new PaginationVO<>();

        paginationVO.setTotal(bidInfoMapper.selectTotal(paramMap));
        paginationVO.setDataList(bidInfoMapper.selectBidInfoByPage(paramMap));

        return paginationVO;
    }
}
