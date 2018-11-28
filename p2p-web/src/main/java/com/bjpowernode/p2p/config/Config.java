package com.bjpowernode.p2p.config;

/**
 * ClassName:config
 * Package:com.bjpowernode.p2p.config
 * Description:
 * Date:2018/3/20 19:45
 * Author:13651027050
 */
public class Config {
    /**
     * pay工程的支付宝支付接口URL
     */
    private String p2p_pay_alipay_url;

    /**
     * p2p调用pay接口查询订单信息地址
     * @return
     */
    private String p2p_pay_alipay_query_url;

    public String getP2p_pay_alipay_query_url() {
        return p2p_pay_alipay_query_url;
    }

    public void setP2p_pay_alipay_query_url(String p2p_pay_alipay_query_url) {
        this.p2p_pay_alipay_query_url = p2p_pay_alipay_query_url;
    }

    public String getP2p_pay_alipay_url() {
        return p2p_pay_alipay_url;
    }

    public void setP2p_pay_alipay_url(String p2p_pay_alipay_url) {
        this.p2p_pay_alipay_url = p2p_pay_alipay_url;
    }
}
