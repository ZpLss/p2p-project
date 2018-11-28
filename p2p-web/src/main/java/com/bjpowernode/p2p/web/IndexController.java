package com.bjpowernode.p2p.web;



import com.bjpowernode.common.constant.Constants;
import com.bjpowernode.p2p.model.loan.LoanInfo;
import com.bjpowernode.p2p.service.loan.BidInfoService;
import com.bjpowernode.p2p.service.loan.LoanInfoService;
import com.bjpowernode.p2p.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * ClassName:IndexController
 * Package:com.bjpowernode.p2p.web
 * Description:
 * Date:2018/3/8 22:01
 * Author:13651027050
 */
@Controller
public class IndexController {

    @Autowired
    private LoanInfoService loanInfoService;
    @Autowired
    private UserService userService;
    @Autowired
    private BidInfoService bidInfoService;

    @RequestMapping(value="/index")
    public String index(HttpServletRequest request, Model model){
        //查询产品平均年化收益率
        Double historyAverageRate = loanInfoService.queryHistoryAverageRate();
        model.addAttribute(Constants.HISTORY_AVERAGE_RATE,historyAverageRate);

        //查询平台注册总人数
        Long allUserCount = userService.queryAllUserCount();
        model.addAttribute(Constants.ALL_USER_COUNT,allUserCount);


        //查询平台注册总金额
        Double allBidMoney = bidInfoService.queryAllBidMoney();
        model.addAttribute("allBidMoney",allBidMoney);


        //准备请求参数
        Map<String,Object> paramMap = new ConcurrentHashMap<>();
        paramMap.put("currentPage",0);//页码

        //查询新手宝,产品类型为0,显示第一页,每页显示一个
        paramMap.put("pageSize",1);//每页条数
        paramMap.put("productType",Constants.PRODUCT_TYPE_X);//产品类型
        List<LoanInfo> xLoanInfoList = loanInfoService.queryLoanInfoTopByProductType(paramMap);

        //查询优先产品,类型为 1
        paramMap.put("productType",Constants.PRODUCT_TYPE_Y);
        paramMap.put("pageSize",4);//每页显示4条
        List<LoanInfo> yLoanInfoList = loanInfoService.queryLoanInfoTopByProductType(paramMap);

        //查询散标产品,类型为2
        paramMap.put("productType",Constants.PRODUCT_TYPE_S);
        paramMap.put("pageSize",8);//每页显示8条
        List<LoanInfo> sLoanInfoList = loanInfoService.queryLoanInfoTopByProductType(paramMap);

        model.addAttribute("xLoanInfoList",xLoanInfoList);
        model.addAttribute("yLoanInfoList",yLoanInfoList);
        model.addAttribute("sLoanInfoList",sLoanInfoList);
        return "index";
    }
}
