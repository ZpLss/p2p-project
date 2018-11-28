package com.bjpowernode.p2p.pay;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.bjpowernode.config.Config;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * ClassName:AliPayController
 * Package:com.bjpowernode.p2p.pay
 * Description:
 * Date:2018/3/20 16:37
 * Author:13651027050
 */
@Controller
public class AliPayController {
    @Autowired
    private Config config;

    private Logger logger = LogManager.getLogger(AliPayController.class);

    @RequestMapping(value = "/api/alipay")
    public String alipay(HttpServletRequest request, Model model,
                         @RequestParam(value = "out_trade_no",required = true)String out_trade_no,
                         @RequestParam(value = "total_amount",required = true)Double total_amount,
                         @RequestParam(value = "subject",required = true)String subject,
                         @RequestParam(value = "body",required = true)String body) throws AlipayApiException {

        System.out.println("----------");

        //pay工程调用支付宝接口

        //获得初始化的AlipayClient
        AlipayClient alipayClient = new DefaultAlipayClient(
                config.getGatewayUrl(),
                config.getApp_id(),
                config.getMerchant_private_key(),
                config.getFormat(),
                config.getCharset(),
                config.getAlipay_public_key(),
                config.getSign_type());

        //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(config.getReturn_url());//同步返回地址
        alipayRequest.setNotifyUrl(config.getNotify_url());//异步返回地址


        alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
                + "\"total_amount\":\""+ total_amount +"\","
                + "\"subject\":\""+ subject +"\","
                + "\"body\":\""+ body +"\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        //若想给BizContent增加其他可选请求参数，以增加自定义超时时间参数timeout_express来举例说明
        //alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
        //		+ "\"total_amount\":\""+ total_amount +"\","
        //		+ "\"subject\":\""+ subject +"\","
        //		+ "\"body\":\""+ body +"\","
        //		+ "\"timeout_express\":\"10m\","
        //		+ "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");
        //请求参数可查阅【电脑网站支付的API文档-alipay.trade.page.pay-请求参数】章节

        //请求
        String result = alipayClient.pageExecute(alipayRequest).getBody();

        System.out.println(result);

        model.addAttribute("result",result);

        return "toAlipay";
    }

    @RequestMapping(value = "/api/alipayBack")
    public String aliPayBack(HttpServletRequest request,Model model
                             ) throws UnsupportedEncodingException, AlipayApiException {

        //获取支付宝GET过来反馈信息
        Map<String,String> params = new HashMap<String,String>();
        Map<String,String[]> requestParams = request.getParameterMap();
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用
            valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }

        //调用SDK验证签名
        boolean signVerified = AlipaySignature.rsaCheckV1(params,
                config.getAlipay_public_key(),
                config.getCharset(),
                config.getSign_type());

        //——请在这里编写您的程序（以下代码仅作参考）——
        if(signVerified) {
            //商户订单号
            String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"),"UTF-8");

            //支付宝交易号
            String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"),"UTF-8");

            //付款金额
            String total_amount = new String(request.getParameter("total_amount").getBytes("ISO-8859-1"),"UTF-8");

            logger.info("trade_no:"+trade_no+"<br/>out_trade_no:"+out_trade_no+"<br/>total_amount:"+total_amount);

            model.addAttribute("signVerified","yes");
        }else {
            logger.info("验签失败");
            model.addAttribute("signVerified","no");
        }
        model.addAttribute("params",params);
        model.addAttribute("p2p_pay_return_url",config.getP2p_pay_return_url());

        return "toP2P";
    }

    @RequestMapping(value = "/api/alipayQuery")
    public @ResponseBody Object alipayQuery(HttpServletRequest request,
                                            @RequestParam(value = "out_trade_no",required = true)String out_trade_no) throws AlipayApiException {

        System.out.println("-----api/alipayQuery-----");

        //获得初始化的AlipayClient
        AlipayClient alipayClient = new DefaultAlipayClient(
                config.getGatewayUrl(),
                config.getApp_id(),
                config.getMerchant_private_key(),
                config.getFormat(),
                config.getCharset(),
                config.getAlipay_public_key(),
                config.getSign_type());

        //设置请求参数
        AlipayTradeQueryRequest alipayRequest = new AlipayTradeQueryRequest();

        alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no+"\"}");

        //请求
        String result = alipayClient.execute(alipayRequest).getBody();

        //输出
        System.out.println(result);
        return result;
    }

}
