package com.bjpowernode.p2p.service.loan;

import com.bjpowernode.common.constant.Constants;
import com.bjpowernode.p2p.mapper.loan.LoanInfoMapper;
import com.bjpowernode.p2p.model.loan.LoanInfo;
import com.bjpowernode.p2p.model.vo.PaginationVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * ClassName:LoanInfoServiceImpl
 * Package:com.bjpowernode.p2p.service.loan
 * Description:
 * Date:2018/3/8 22:35
 * Author:13651027050
 */
@Service("loanInfoServiceImpl")
public class LoanInfoServiceImpl implements LoanInfoService {

    @Autowired
    private RedisTemplate<String, Serializable> redisTemplate;

    @Autowired
    private LoanInfoMapper loanInfoMapper;


    @Override
    public Double queryHistoryAverageRate() {
        //首先去redis缓存中查询,有的化直接用,没有需要去数据库查询,并且存放到redis缓存中,
        //好处,减少对数据库的访问,提升了系统的性能
        BoundValueOperations<String, Serializable> boundValueOps = redisTemplate.boundValueOps(Constants.HISTORY_AVERAGE_RATE);

        //获取该操作对象所对应的value
        Double historyAverageRate = (Double) boundValueOps.get();

        //判断是否为空
        if (null == historyAverageRate) {
            //缓存中没有值,从数据库中查询
            historyAverageRate = loanInfoMapper.selectHistoryAverageRate();

            //将查询的结果放到redis缓存中
            boundValueOps.set(historyAverageRate);

            //设置失效时间
            boundValueOps.expire(15, TimeUnit.MINUTES);
        }

        return historyAverageRate;

    }

    @Override
    public List<LoanInfo> queryLoanInfoTopByProductType(Map<String, Object> paramMap) {


        return loanInfoMapper.selectLoanInfoByPage(paramMap);
    }

    @Override
    public PaginationVO<LoanInfo> queryLoanInfoByPage(Map<String, Object> paramMap) {

        //查询产品列表
        List<LoanInfo> loanInfoList = loanInfoMapper.selectLoanInfoByPage(paramMap);
        //根据productType查询产品总数量
        Long total = loanInfoMapper.selectLoanInfoCountByProductType(paramMap);

        //把查询结果封装返回
        PaginationVO<LoanInfo> paginationVO = new PaginationVO<>();
        paginationVO.setTotal(total);
        paginationVO.setDataList(loanInfoList);

        return paginationVO;
    }

    @Override
    public LoanInfo queryLoanInfoByLoanId(Integer loanId) {

        LoanInfo loanInfo = loanInfoMapper.selectByPrimaryKey(loanId);


        return loanInfo;
    }


}
