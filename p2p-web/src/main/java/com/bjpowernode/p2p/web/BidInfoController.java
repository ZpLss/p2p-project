package com.bjpowernode.p2p.web;

import com.bjpowernode.common.constant.Constants;
import com.bjpowernode.p2p.model.loan.BidInfo;
import com.bjpowernode.p2p.model.user.User;
import com.bjpowernode.p2p.model.vo.PaginationVO;
import com.bjpowernode.p2p.model.vo.ResultObject;
import com.bjpowernode.p2p.service.loan.BidInfoService;
import com.sun.corba.se.impl.orbutil.closure.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ClassName:BidInfoController
 * Package:com.bjpowernode.p2p.web
 * Description:
 * Date:2018/3/15 15:11
 * Author:13651027050
 */
@Controller
public class BidInfoController {

    @Autowired
    private BidInfoService bidInfoService;

    @RequestMapping(value = "/loan/invest")
    public @ResponseBody Object invest(HttpServletRequest request,
                                       @RequestParam(value = "loanId",required = true)Integer loanId,
                                       @RequestParam(value = "bidMoney",required = true)Double bidMoney){

        //获取用户信息
        User sessionUser = (User) request.getSession().getAttribute(Constants.SESSION_USER);

        //封装参数
        Map<String,Object> paramMap = new ConcurrentHashMap<>();
        paramMap.put("uid",sessionUser.getId());
        paramMap.put("loanId",loanId);
        paramMap.put("bidMoney",bidMoney);

        paramMap.put("phone",sessionUser.getPhone());

        //调用业务层的投资方法
        ResultObject resultObject = bidInfoService.invest(paramMap);
        //判断
        Map<String,Object> retMap = new HashMap<>();
        if (Constants.SUCCESS.equals(resultObject.getErrorCode())){
            //投资成功
            retMap.put(Constants.ERROR_MESSAGE,"ok");
        }else {
            retMap.put(Constants.ERROR_MESSAGE,"投资失败");
        }
        return retMap;

    }

    @RequestMapping(value = "/loan/myInvest")
    public String myInvest(HttpServletRequest request, Model model,
                           @RequestParam(value = "currentPage",required = false)Integer currentPage){
        //从session中获取用户信息
        User user = (User) request.getSession().getAttribute(Constants.SESSION_USER);
        int uid = user.getId();
        //判断当前页码是否有值
        if (null == currentPage){
            currentPage = 1;//默认第一页
        }

        //准备分页查询参数
        Map<String,Object> paramMap = new HashMap<>();
        paramMap.put("currentPage",(currentPage-1)*5);
        paramMap.put("pageSize",5);
        paramMap.put("uid",uid);

        //调用分页查询投资列表


        //查询全部的投资记录
        PaginationVO<BidInfo> paginationVO = bidInfoService.queryAllBidInfoByPage(paramMap);

        //计算总页数
        int totalPage = paginationVO.getTotal().intValue()/ 5 ;
        int mod = paginationVO.getTotal().intValue() % 5;
        if (mod > 0){
            totalPage = totalPage + 1;
        }

        model.addAttribute("currentPage",currentPage);
        model.addAttribute("totalPage",totalPage);
        model.addAttribute("totalRows",paginationVO.getTotal());
        model.addAttribute("dataList",paginationVO.getDataList());

        return "myInvest";
    }


}
