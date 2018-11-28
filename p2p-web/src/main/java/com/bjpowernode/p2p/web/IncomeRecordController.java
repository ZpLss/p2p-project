package com.bjpowernode.p2p.web;

import com.bjpowernode.common.constant.Constants;
import com.bjpowernode.p2p.model.loan.IncomeRecord;
import com.bjpowernode.p2p.model.user.User;
import com.bjpowernode.p2p.model.vo.PaginationVO;
import com.bjpowernode.p2p.service.loan.IncomeRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * ClassName:IncomeRecordController
 * Package:com.bjpowernode.p2p.web
 * Description:
 * Date:2018/3/16 16:46
 * Author:13651027050
 */
@Controller
public class IncomeRecordController {
    @Autowired
    private IncomeRecordService incomeRecordService;

    @RequestMapping(value = "/loan/myIncome")
    public String myIncomeRecord(HttpServletRequest request, Model model,
                                 @RequestParam(value = "currentPage" ,required = false)Integer currentPage){
        //从session中获取用户信息
        User user = (User) request.getSession().getAttribute(Constants.SESSION_USER);
        int uid = user.getId();
        if (null == currentPage){
            currentPage = 1;
        }

        //准备分页查询参数
        Map<String,Object> paramMap = new HashMap<>();
        paramMap.put("uid",uid);
        paramMap.put("currentPage",(currentPage-1)*5);//跳过的数据条数
        paramMap.put("pageSize",5);//每页的条数

        //分页查询用户收益记录
        PaginationVO<IncomeRecord> paginationVO = incomeRecordService.queryIncomeRecordByPage(paramMap);

        //计算总页数
        int totalPage = paginationVO.getTotal().intValue() / 5;
        int mod = paginationVO.getTotal().intValue() % 5;
        if (mod > 0){
            totalPage = totalPage + 1;
        }

        //把数据返回页面
        model.addAttribute("currentPage",currentPage);
        model.addAttribute("totalPage",totalPage);
        model.addAttribute("pageSize",5);
        model.addAttribute("totalRows",paginationVO.getTotal().intValue());
        model.addAttribute("dataList",paginationVO.getDataList());

        return "myIncome";
    }
}
