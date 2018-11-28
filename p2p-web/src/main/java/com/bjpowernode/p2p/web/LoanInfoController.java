package com.bjpowernode.p2p.web;

import com.bjpowernode.common.constant.Constants;
import com.bjpowernode.p2p.model.loan.BidInfo;
import com.bjpowernode.p2p.model.loan.LoanInfo;
import com.bjpowernode.p2p.model.user.FinanceAccount;
import com.bjpowernode.p2p.model.user.User;
import com.bjpowernode.p2p.model.vo.PaginationVO;
import com.bjpowernode.p2p.model.vo.UserTop;
import com.bjpowernode.p2p.service.loan.BidInfoService;
import com.bjpowernode.p2p.service.loan.LoanInfoService;
import com.bjpowernode.p2p.service.user.FinanceAccountService;
import com.bjpowernode.p2p.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * ClassName:LoanInfoController
 * Package:com.bjpowernode.p2p.web
 * Description:
 * Date:2018/3/10 17:42
 * Author:13651027050
 */
@Controller
public class LoanInfoController {

    @Autowired
    private LoanInfoService loanInfoService;
    @Autowired
    private BidInfoService bidInfoService;
    @Autowired
    private UserService userService;
    @Autowired
    private FinanceAccountService financeAccountService;

    @RequestMapping(value = "/loan/loan")
    public String loan(HttpServletRequest request, Model model,
                        @RequestParam( value = "ptype",required=false) Integer productType,
                        @RequestParam (value = "currentPage",required=false) Integer currentPage){
        //判断currentPage是否为空,空,默认为第一页
        if (null == currentPage){
            currentPage = 1;//默认为第一页
        }
        //准备分页查参数
        Map<String,Object> paramMap = new ConcurrentHashMap<>();
        if (productType != null) {
            paramMap.put("productType",productType);
        }
        paramMap.put("currentPage",currentPage);
        paramMap.put("pageSize", Constants.PAGE_SIZE);

        //分页查询产品信息列表--返回内容包括,产品信息,产品数量PaginationVO(分页模型对象)
        PaginationVO<LoanInfo> paginationVOList = loanInfoService.queryLoanInfoByPage(paramMap);

        //计算总页数
        int totalPage = paginationVOList.getTotal().intValue()/ Constants.PAGE_SIZE;
        int mod = paginationVOList.getTotal().intValue()% Constants.PAGE_SIZE;
        if (mod > 0){
            totalPage += 1;
        }
        //将以上查询结果存放到model对象中
        model.addAttribute("loanInfoList",paginationVOList.getDataList());//产品信息
        model.addAttribute("totalPage",totalPage);//总页数
        model.addAttribute("currentPage",currentPage);//当前页
        model.addAttribute("pageSize",Constants.PAGE_SIZE);//每页条数
        model.addAttribute("totalRows",paginationVOList.getTotal());//总条数

        //查询用户投资排行榜
        List<UserTop> userTopList = bidInfoService.queryBidUserTop();
        model.addAttribute("userTopList",userTopList);


        return "loan";
    }

    @RequestMapping(value = "/loan/loanInfo")
    public String loanInfo(HttpServletRequest request,Model model,
                            @RequestParam (value="loanId",required=true)Integer loanId){

        //获取产品详细信息
        LoanInfo loanInfo = loanInfoService.queryLoanInfoByLoanId(loanId);

        //获取产品的投资信息
        List<BidInfo> bidInfoList = bidInfoService.queryBidInfoByLoanId(loanId);

        model.addAttribute("loanInfo",loanInfo);
        model.addAttribute("bidInfoList",bidInfoList);

        //获取用户的账户金额

        User user = (User) request.getSession().getAttribute(Constants.SESSION_USER);
        if (null != user){
            FinanceAccount financeAccount = financeAccountService.queryMyFinanceAcountByUid(user.getId());
            model.addAttribute("financeAccount",financeAccount);
        }

        return "loanInfo";
    }

    @RequestMapping(value = "/loan/loadStat")
    public @ResponseBody Object loadStat(HttpServletRequest request,Model model){

        //查询历史年化收益率
        Double historyAverageRate = loanInfoService.queryHistoryAverageRate();

        //擦汗寻平台用户数
        Long allUserCount = userService.queryAllUserCount();

        //查询投资总金额
        Double allBidMoney = bidInfoService.queryAllBidMoney();

        Map<String,Object> retMap = new HashMap<>();
        retMap.put(Constants.HISTORY_AVERAGE_RATE,historyAverageRate);
        retMap.put(Constants.ALL_USER_COUNT,allUserCount);
        retMap.put(Constants.ALL_BID_MONEY,allBidMoney);

        return retMap;
    }
}
