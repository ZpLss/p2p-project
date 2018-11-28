package com.bjpowernode.p2p.web;

import com.alibaba.fastjson.JSONObject;
import com.bjpowernode.common.constant.Constants;
import com.bjpowernode.common.util.DateUtils;
import com.bjpowernode.common.util.HttpClientTest;
import com.bjpowernode.p2p.config.Config;
import com.bjpowernode.p2p.model.loan.RechargeRecord;
import com.bjpowernode.p2p.model.user.User;
import com.bjpowernode.p2p.model.vo.PaginationVO;
import com.bjpowernode.p2p.service.loan.OnlyNumberService;
import com.bjpowernode.p2p.service.loan.RechargeRecordService;
import com.bjpowernode.p2p.service.user.FinanceAccountService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ClassName:RechargeRecordController
 * Package:com.bjpowernode.p2p.web
 * Description:
 * Date:2018/3/16 15:45
 * Author:13651027050
 */
@Controller
public class RechargeRecordController {

    @Autowired
    private RechargeRecordService rechargeRecordService;
    @Autowired
    private OnlyNumberService onlyNumberService;
    @Autowired
    private Config config;
    @Autowired
    private FinanceAccountService financeAccountService;

    @RequestMapping(value = "/loan/myRecharge")
    public String myRecharge(HttpServletRequest request, Model model,
                             @RequestParam(value = "currentPage",required = false)Integer currentPage){
        //分页查询用户最近充值记录
        //从session中获取用户信息
        User user = (User) request.getSession().getAttribute(Constants.SESSION_USER);
        int uid = user.getId();

        //准备参数,分页查询充值记录
        Map<String,Object> paramMap = new ConcurrentHashMap<>();
        if (null == currentPage){
            currentPage = 1;
        }
        paramMap.put("uid",uid);
        paramMap.put("currentPage",(currentPage -1)*5);
        paramMap.put("pageSize",5);

        PaginationVO<RechargeRecord> paginationVO = rechargeRecordService.queryRechargeRecordByPage(paramMap);

        //计算总页数
        int totalPage = paginationVO.getTotal().intValue();
        int mod = paginationVO.getTotal().intValue() % 5;
        if (mod > 0){
            totalPage= totalPage + 1;
        }

        //把数据返回页面
        model.addAttribute("currentPage",currentPage);//当前页
        model.addAttribute("totalPage",totalPage);//总页数
        model.addAttribute("pageSize",5);//每页条数
        model.addAttribute("totalRows",paginationVO.getTotal().intValue());//总条数
        model.addAttribute("dataList",paginationVO.getDataList());//充值记录

        return "myRecharge";
    }

    @RequestMapping(value="/loan/toRecharge")
    public String toAliPayRecharge(HttpServletRequest request,Model model,
                                   @RequestParam(value = "rechargeMoney",required = true)Double rechargeMoney){

        //System.out.println("-----------toAlipay-------" + rechargeMoney);
        //从sessiopn中获取用户信息
        User user = (User) request.getSession().getAttribute(Constants.SESSION_USER);
        //生成一个全局唯一的充值订单号 = 时间戳 + redis下的全局唯一数
        String rechargeNo = DateUtils.getTime() + onlyNumberService.getOnlyNumber();

        //创建充值记录(状态为0 ,充值中)
        RechargeRecord rechargeRecord = new RechargeRecord();
        rechargeRecord.setUid(user.getId());//用户标识
        rechargeRecord.setRechargeDesc("充值-支付宝充值");//充值描述
        rechargeRecord.setRechargeNo(rechargeNo);//充值编号
        rechargeRecord.setRechargeMoney(rechargeMoney);
        rechargeRecord.setRechargeStatus("0");//充值状态  1:充值成功  2:充值失败
        rechargeRecord.setRechargeTime(new Date());//充值时间

        int addRechargeRecordCount = rechargeRecordService.addRechargeRecord(rechargeRecord);

        if (addRechargeRecordCount > 0){
            //p2p调用pay工程提供的支付宝支付接口(按照pay工程支付宝接口要求上传参数)
            model.addAttribute("p2p_pay_alipay_url",config.getP2p_pay_alipay_url());
            model.addAttribute("rechargeNo",rechargeNo);//充值订单号
            model.addAttribute("rechargeMoney",rechargeMoney);//充值金额
            model.addAttribute("subject","充值");
            model.addAttribute("body","支付宝充值");

        }else {
            //失败
            model.addAttribute("trade_msg","充值失败");
            return "toRechargeBack";
        }
        return "toAliPay";
    }

    @RequestMapping(value="/loan/alipayBack")
    public String alipayBack(HttpServletRequest request,Model model,
                             @RequestParam(value = "out_trade_no",required =true)String out_trade_no,
                             @RequestParam(value = "signVerified",required = true)String signVerified,
                             @RequestParam(value = "total_amount",required = true)String totalAmount){

        System.out.println("-------loan/alipayBack" + out_trade_no);

        //判断验证签名是否成功
        if ("yes".equals(signVerified)){
            //p2p调用pay工程的订单查询接口,按照订单接口要求上传参数

            //pay工程的订单查询接口返回的是:json格式的字符串,解析json格式字符串
            Map<String,Object> paramMap = new ConcurrentHashMap<>();
            paramMap.put("out_trade_no",out_trade_no);
            String result = HttpClientTest.doPost(config.getP2p_pay_alipay_query_url(),paramMap);

            //1.将json搁置的字符串转换为json对象
            JSONObject jsonObject = JSONObject.parseObject(result);
            //2.获取指定key:alipay_trade_query_response的值
            JSONObject tradeQueryResponse = jsonObject.getJSONObject("alipay_trade_query_response");
            //3.获取code所对应的value,该值表示通信是否成功(10000表示成功)
            String code = tradeQueryResponse.getString("code");
            //判断通信是否成功
            if ("10000".equals(code)){
                //4.获取trade_status,交易状态
                String tradeStatus = tradeQueryResponse.getString("trade_status");
                //判断充值交易是否成功
                /*交易状态：WAIT_BUYER_PAY（交易创建，等待买家付款）、
                TRADE_CLOSED（未付款交易超时关闭，或支付完成后全额退款）、
                TRADE_SUCCESS（交易支付成功）、
                TRADE_FINISHED（交易结束，不可退款）*/

                if ("TRADE_CLOSED".equals(tradeStatus)){
                    //更新当前充值的状态为:2.充值失败
                    paramMap.put("rechargeNo",out_trade_no);
                    paramMap.put("rechargeStatus","2");
                    rechargeRecordService.modifyRechargeRecordByRechargeNo(paramMap);
                    model.addAttribute("trade_msg","充值失败");
                    return "toRechargeBack";
                }
                if("TRADE_SUCCESS".equals(tradeStatus) || "TRADE_FINISHED".equals(tradeStatus)){
                    //充值成功,更新用户当前账户可用余额
                    User user = (User) request.getSession().getAttribute(Constants.SESSION_USER);
                    paramMap.put("uid",user.getId());
                    paramMap.put("rechargeMoney",totalAmount);
                    paramMap.put("rechargeStatus","1");
                    int rechargeCount = rechargeRecordService.recharge(paramMap);
                    if (rechargeCount <= 0){
                        //充值失败
                        model.addAttribute("trade_msg","充值失败");
                        return "toRechargeBack";
                    }
                }
            }else {
                model.addAttribute("trade_msg","充值失败");
                return "toRechargeBack";
            }

        }else {
            model.addAttribute("trade_msg","验证签名失败");
            return "toRechargeBack";
        }
        return "redirect:/loan/myRecharge";
    }

    @RequestMapping(value = "/loan/toWXpayRecharge")
    public String wxpayRecharge(HttpServletRequest request,Model model,
                                @RequestParam(value ="rechargeMoney",required = true)Double rechargeMoney){
        //从session中获取用户信息
        User user = (User) request.getSession().getAttribute(Constants.SESSION_USER);

        //生成充值记录
        RechargeRecord rechargeRecord = new RechargeRecord();
        rechargeRecord.setUid(user.getId());
        //生成一个全局唯一的充值订单号 == 时间戳 + redis全局唯一数字
        String rechargeNo = DateUtils.getTime() + onlyNumberService.getOnlyNumber();
        rechargeRecord.setRechargeNo(rechargeNo);
        rechargeRecord.setRechargeMoney(rechargeMoney);
        rechargeRecord.setRechargeTime(new Date());
        rechargeRecord.setRechargeStatus("0");
        rechargeRecord.setRechargeDesc("充值-微信支付");

        int addRechargeCount = rechargeRecordService.addRechargeRecord(rechargeRecord);
        if (addRechargeCount > 0){
            //成功
            model.addAttribute("rechargeNo",rechargeNo);
            model.addAttribute("rechargeMoney",rechargeMoney);
            model.addAttribute("rechargeTime",new Date());

        }else {
            //失败
            model.addAttribute("trade_msg","充值失败");
            return "toRechargeBack";
        }


        return "showQRCode";
    }

    @RequestMapping(value = "/loan/generateQRCode")
    public void generateQRCode(HttpServletRequest request, HttpServletResponse response,
                               @RequestParam(value = "out_trade_no",required= true)String rechargeNo,
                               @RequestParam(value = "rechargeMoney",required = true)Double rechargeMoney) throws IOException, WriterException {
        //调用pay工程的微信支付接口
        Map<String,Object> paramMap = new HashMap<>();
        BigDecimal total_fee = new BigDecimal(rechargeMoney).multiply(new BigDecimal(100));
        paramMap.put("total_fee",total_fee);
        paramMap.put("out_trade_no",rechargeNo);
        paramMap.put("body","微信支付");

        //pay工程微信支付接口返回的json格式的字符串
        String result = HttpClientTest.doPost("http://localhost:9090/pay/api/wxpay",paramMap);

        //解析result
        JSONObject jsonObject = JSONObject.parseObject(result);

        //获取return_code
        String return_code = jsonObject.getString("return_code");

        //判断通信是否成功
        if ("SUCCESS".equals(return_code)){
            //获取result_code业务处理结果
            String result_code = jsonObject.getString("result_code");
            //判断业务处理结果
            if ("SUCCESS".equals(result_code)){
                //获取code_url值
                String code_url = jsonObject.getString("code_url");

                //将code_url值生成二维码图片
                //二维码的宽高
                int width = 200;
                int hight = 200;

                //创建map集合
                Map<EncodeHintType,Object> hints = new HashMap<EncodeHintType,Object>();
                hints.put(EncodeHintType.CHARACTER_SET,"UTF-8");

                //创建一个矩阵对象
                BitMatrix bitMatrix = new MultiFormatWriter().encode(code_url, BarcodeFormat.QR_CODE,width,hight,hints);

                //创建字节数组输出流
                ByteArrayOutputStream imageOut = new ByteArrayOutputStream();

                //将矩阵对象转换为流响应到页面
                MatrixToImageWriter.writeToStream(bitMatrix,"jpg",imageOut);

                //字节数据输入流
                ByteArrayInputStream imageIn = new ByteArrayInputStream(imageOut.toByteArray());

                //创建一个图片缓存对象
                BufferedImage bImage = ImageIO.read(imageIn);

                //输出流对象
                OutputStream outputStream = response.getOutputStream();
                ImageIO.write(bImage,"jpg",outputStream);

                bImage.flush();
                outputStream.flush();
                outputStream.close();

            }else {
                //失败
                String contextPath = request.getContextPath();
                response.sendRedirect(contextPath + "/toRechargeBack.jsp");
            }
        }else {
            //失败
            String contextPath = request.getContextPath();
            response.sendRedirect(contextPath + "/toRechargeBack.jsp");

        }

    }
}
