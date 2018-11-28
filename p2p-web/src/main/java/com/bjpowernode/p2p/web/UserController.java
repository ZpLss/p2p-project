package com.bjpowernode.p2p.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bjpowernode.common.constant.Constants;
import com.bjpowernode.common.util.HttpClientTest;
import com.bjpowernode.p2p.model.loan.BidInfo;
import com.bjpowernode.p2p.model.loan.IncomeRecord;
import com.bjpowernode.p2p.model.loan.RechargeRecord;
import com.bjpowernode.p2p.model.user.FinanceAccount;
import com.bjpowernode.p2p.model.user.User;
import com.bjpowernode.p2p.model.vo.ResultObject;
import com.bjpowernode.p2p.service.loan.BidInfoService;
import com.bjpowernode.p2p.service.loan.IncomeRecordService;
import com.bjpowernode.p2p.service.loan.RechargeRecordService;
import com.bjpowernode.p2p.service.user.FinanceAccountService;
import com.bjpowernode.p2p.service.user.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * ClassName:UserController
 * Package:com.bjpowernode.p2p.web
 * Description:
 * Date:2018/3/11 13:21
 * Author:13651027050
 */
@Controller
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private FinanceAccountService financeAccountService;
    @Autowired
    private BidInfoService bidInfoService;
    @Autowired
    private RechargeRecordService rechargeRecordService;
    @Autowired
    private IncomeRecordService incomeRecordService;


    @RequestMapping(value = "/loan/checkPhone")
    public @ResponseBody Object checkPhone(HttpServletRequest request,
                                           @RequestParam(value="phone",required = true)String phone){
        //验证手机号是否重复
        //根据手机号查询用户
        User user = userService.queryUserByPhone(phone);
        //判断
        Map<String,Object> retMap = new ConcurrentHashMap<>();

        if (null != user){
            retMap.put(Constants.ERROR_MESSAGE,"该手机已经注册,请更换号码!");
            return retMap;
        }

        retMap.put(Constants.ERROR_MESSAGE,"ok");

        return retMap;
    }

    @RequestMapping(value="/loan/checkCaptcha")
    public @ResponseBody Object checkCaptcha(HttpServletRequest request,
                                             @RequestParam(value="captcha",required = true)String captcha){

        Map<String,Object> retMap = new ConcurrentHashMap<>();

        //获取图形验证码
        String sessionCaptcha = (String) request.getSession().getAttribute(Constants.CAPTCHA);

        if (StringUtils.equalsIgnoreCase(sessionCaptcha,captcha)) {
            retMap.put(Constants.ERROR_MESSAGE,"ok");
        }else{
            retMap.put(Constants.ERROR_MESSAGE,"请输入正确的图形验证码");
        }
        return retMap;
    }

    @RequestMapping(value = "/loan/register")
    public @ResponseBody Object registerUser(HttpServletRequest request,
                                             @RequestParam(value = "phone",required = true)String phone,
                                             @RequestParam(value = "loginPassword",required = true) String loginPassword,
                                             @RequestParam(value = "replayLoginPassword", required = true) String replayLoginPassword){

        Map<String,Object> retMap = new ConcurrentHashMap<>();

        //后台对参数再次验证
        if (!Pattern.matches("^1[1-9]\\d{9}$",phone)){
            retMap.put(Constants.ERROR_MESSAGE,"请输入正确的手机号码");
            return retMap;
        }

        User user = userService.queryUserByPhone(phone);
        if (user != null) {
            retMap.put(Constants.ERROR_MESSAGE,"该手机号码已经注册,请换一个手机号码");
            return retMap;
        }

        if (!StringUtils.equals(loginPassword,replayLoginPassword)){
            retMap.put(Constants.ERROR_MESSAGE,"两次输入的密码不同");
            return retMap;
        }
        //------用户注册--------
        User userInfo = new User();
        userInfo.setPhone(phone);
        userInfo.setLoginPassword(loginPassword);

        ResultObject resultObject = userService.register(userInfo);
        if (resultObject.getErrorCode().equals(Constants.SUCCESS)){
            //注册成功,把用户信息保存到session中
            User sessionUser  = userService.queryUserByPhone(phone);
            request.getSession().setAttribute(Constants.SESSION_USER,sessionUser);
            retMap.put(Constants.ERROR_MESSAGE,"ok");
        }else {
            retMap.put(Constants.ERROR_MESSAGE,"注册失败");
        }

        return retMap;
    }

    @RequestMapping(value = "/loan/myFinanceAcount")
    public @ResponseBody Object myFinanceAcount(HttpServletRequest request){

        //从session中获取用户的id
        User user = (User) request.getSession().getAttribute(Constants.SESSION_USER);
        int uid = user.getId();
        //查询账户的可用金额
        FinanceAccount financeAccount = financeAccountService.queryMyFinanceAcountByUid(uid);

        return financeAccount;
    }

    @RequestMapping(value = "/loan/verifyRealName")
    public @ResponseBody Object verifyRealName(HttpServletRequest request,
                                               @RequestParam(value ="realName",required = true)String realName,
                                               @RequestParam(value ="idCard",required = true) String idCard,
                                               @RequestParam(value="replayIdCard",required = true) String replayIdCard){

        Map<String,Object> retMap = new ConcurrentHashMap<>();
        //验证姓名
        if(!Pattern.matches("[\\u4e00-\\u9fa5]+",realName)){
            retMap.put(Constants.ERROR_MESSAGE,"真实姓名只支持中文");
            return retMap;
        }
        //验证身份证号码
        if(!Pattern.matches("(^\\d{15}$)|(^\\d{18}$)|(^\\d{17}(\\d|X|x)$)",idCard)){
            retMap.put(Constants.ERROR_MESSAGE,"请输入正确的身份证号码");
            return retMap;
        }

        if (!(idCard.length() == 15 || idCard.length() == 18)) {
            retMap.put(Constants.ERROR_MESSAGE,"请输入正确的身份证号码");
            return retMap;
        }

        if (!StringUtils.equals(idCard,replayIdCard)){
            retMap.put(Constants.ERROR_MESSAGE,"两次输入的身份证号码不一致");
            return retMap;
        }

        //实名认证

        Map<String,Object> paramMap = new ConcurrentHashMap<>();

        paramMap.put("appkey", "8ec6a5b4021b05011dd426703abff572");
        paramMap.put("realName", realName);
        paramMap.put("cardNo", idCard);

        //p2p-web 项目调用互联网接口实名认证  返回的是json格式字符串
        //String result = HttpClientTest.doPost("https://way.jd.com/freedt/api_rest_police_identity", paramMap);
        String result = HttpClientTest.doPost("https://way.jd.com/youhuoBeijing/test", paramMap);

        //String result = "{\"code\": \"10000\",\"charge\": false,\"msg\": \"查询成功\",\"result\": {\"error_code\": 0,\"reason\": \"成功\",\"result\": {\"realname\": \"乐天磊\",\"idcard\": \"350721197702134399\",\"isok\": true}}}";


        //解析json格式的字符串,使用阿里提供的fastJons工具来解析json字符串
        //1.将json格式的字符串,转换为json对象
        JSONObject jsonObject = JSON.parseObject(result);
        
        //2.获取制定的key的value
        String code = jsonObject.getString("code");

        //3.判断是否通信成功:如果code为10000成功
        if ("10000".equals(code)){
            //成功
            //4.获取指定key(result)的value()
            JSONObject resultJsonObject = jsonObject.getJSONObject("result");

            //5.在通过上一部的json对象,获取result的值(该值仍未json)
            JSONObject resultJsonObject2 = resultJsonObject.getJSONObject("result");
            //获取isok的值,给之是true,匹配成功
            Boolean isok = resultJsonObject2.getBoolean("isok");

            if (isok) {
                //从session中获取用户信息
                User user = (User) request.getSession().getAttribute(Constants.SESSION_USER);

                //更新当前用户信息
                paramMap.put("id",user.getId());
                int modifyCount = userService.modifyUserByUserId(paramMap);
                if (modifyCount <=0 ){
                    //失败
                    retMap.put(Constants.ERROR_MESSAGE,"实名认证失败");
                    return retMap;
                }else {
                    User user1 = userService.queryUserByPhone(user.getPhone());
                    //把信息修改后的用户保存到session中
                    request.getSession().setAttribute(Constants.SESSION_USER,user1);

                    retMap.put(Constants.ERROR_MESSAGE,"ok");
                    return retMap;
                }
            }else {
                //失败
                retMap.put(Constants.ERROR_MESSAGE,"实名认证失败");
                return retMap;
            }
        }else {
            //失败
            retMap.put(Constants.ERROR_MESSAGE,"实名认证失败");
        }
        return retMap;
    }

    @RequestMapping(value ="/loan/login")
    public @ResponseBody Object login(HttpServletRequest request,
                                      @RequestParam(value ="phone",required = true)String phone,
                                      @RequestParam(value ="loginPassword",required = true)String loginPassword){
        Map<String,Object> retMap = new ConcurrentHashMap<>();
        //验证手机号
        if (!Pattern.matches("^1[1-9]\\d{9}$",phone)) {
            retMap.put(Constants.ERROR_MESSAGE,"手机号码错误");
            return retMap;
        }

        User user = new User();
        user.setPhone(phone);
        user.setLoginPassword(loginPassword);
        //查询用户是否存在
        User retUser = userService.login(user);

        if (null != retUser) {
            retMap.put(Constants.ERROR_MESSAGE,"ok");

            //把最新的用户信息保存到session中
            User sessionUser = userService.queryUserByPhone(phone);
            request.getSession().setAttribute(Constants.SESSION_USER,sessionUser);

        }else {
            retMap.put(Constants.ERROR_MESSAGE,"用户名或密码错误,请重新输入");
            return retMap;
        }
        return retMap;
    }

    @RequestMapping(value ="/loan/logout")
    public String logout(HttpServletRequest request){
        //将session中的信息清除
        request.getSession().removeAttribute(Constants.SESSION_USER);
        //或者使session失效
        //request.getSession().invalidate();

        return "redirect:/index";
    }@RequestMapping(value ="/loan/loan/logout")
    public String logoutT(HttpServletRequest request){
        //将session中的信息清除
        request.getSession().removeAttribute(Constants.SESSION_USER);
        //或者使session失效
        //request.getSession().invalidate();

        return "redirect:/index";
    }

    @RequestMapping(value ="/loan/myCenter")
    public String myCenter(HttpServletRequest request, Model model){
        //从session中获取用户信息
        User user = (User) request.getSession().getAttribute(Constants.SESSION_USER);
        int uid = user.getId();
        //根据用户标识获取用户账户信息
        FinanceAccount financeAccount = financeAccountService.queryMyFinanceAcountByUid(uid);
        model.addAttribute("financeAccount",financeAccount);

        //请求参数
        Map<String,Object> paramMap = new HashMap<>();
        paramMap.put("currentPage",0);
        paramMap.put("pageSize",5);
        paramMap.put("uid",uid);

        //根据用户标识获取用户最近投资信息,显示第一页0,每页显示5条
        List<BidInfo> bidInfoList = bidInfoService.queryBidInfoTopByUid(paramMap);
        model.addAttribute("bidInfoList",bidInfoList);

        //根据用户标识获取用户最近充值信息
        List<RechargeRecord> rechargeRecordList = rechargeRecordService.queryRechargeRecordByUid(paramMap);
        model.addAttribute("rechargeRecordList",rechargeRecordList);

        //根据用户标识获取用户最近收益信息
        List<IncomeRecord> incomeRecordList = incomeRecordService.queryIncomeRecordByUid(paramMap);
        model.addAttribute("incomeRecordList",incomeRecordList);

        return "myCenter";
    }
}
