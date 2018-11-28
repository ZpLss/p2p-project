package com.bjpowernode.p2p.pay;

import com.bjpowernode.common.util.HttpClientTest;
import com.github.wxpay.sdk.WXPayUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * ClassName:WXpayController
 * Package:com.bjpowernode.p2p.pay
 * Description:
 * Date:2018/3/23 20:53
 * Author:13651027050
 */
@Controller
public class WXpayController {

    @RequestMapping(value = "api/wxpay")
    public @ResponseBody Object wxpay(HttpServletRequest request,
                                      @RequestParam(value = "body",required = true)String body,
                                      @RequestParam(value = "out_trade_no",required = true)String out_trade_no,
                                      @RequestParam(value ="total_fee",required = true)String total_fee) throws Exception {
        System.out.println("1-------wx----pay------");



        Map<String,String> requestDataMap = new HashMap<>();
        requestDataMap.put("appid","wx8a3fcf509313fd74");
        requestDataMap.put("mch_id","1361137902");
        //生成随机字符串
        String nonceStr = WXPayUtil.generateNonceStr();
        requestDataMap.put("nonce_str",nonceStr);


        requestDataMap.put("body",body);//商品描述
        requestDataMap.put("out_trade_no",out_trade_no);//订单号
        requestDataMap.put("total_fee",total_fee);//订单金额,单位为分
        //获取主机地址
        InetAddress localHost = InetAddress.getLocalHost();
        String hostAddress = localHost.getHostAddress();
        requestDataMap.put("spbill_create_ip",hostAddress);
        requestDataMap.put("notify_url","http://localhost:9090/pay/api/wxpayNotifyUrl");
        requestDataMap.put("trade_type","NATIVE");
        requestDataMap.put("product_id",out_trade_no);

        //生成签名

        //String signValue = WXPayUtil.generateSignature(requestDataMap,"367151c5fd0d50f1e34a68a802d6bbca");
        String signValue = WXPayUtil.generateSignature(requestDataMap,"367151c5fd0d50f1e34a68a802d6bbca");
        requestDataMap.put("sign",signValue);

        //将类型为map的参数转换为xml格式
        String requestDataXml = WXPayUtil.mapToXml(requestDataMap);

        //发送调用统一下单api的接口,相应的是xml格式的结果
        String responseDataXml = HttpClientTest.doPostByXml("https://api.mch.weixin.qq.com/pay/unifiedorder", requestDataXml);

        //把xml字符串转化为map集合,有jskon转换为json格式的字符串响应回去
        Map<String, String> responseMap = WXPayUtil.xmlToMap(responseDataXml);

        return responseMap;
    }

}
