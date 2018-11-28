package com.bjpowernode.config;

/**
 * ClassName:AlipayConfig
 * Package:com.bjpowernode.config
 * Description:
 * Date:2018/3/20 21:03
 * Author:13651027050
 */
public class Config {

    /**
     * 支付网关
     */
    private String gatewayUrl;

    //应用Id
    private String app_id;

    private String merchant_private_key;
    private String format;
    private String charset;
    private String alipay_public_key;
    private String sign_type;

    /**
     * pay返回给p2p同步参数的URL
     */
    private String p2p_pay_return_url;

    /**
     * 同步返回地址
     */
    private String return_url;
    /**
     * 异步返回地址
     */
    private String notify_url;



    public String getP2p_pay_return_url() {
        return p2p_pay_return_url;
    }

    public void setP2p_pay_return_url(String p2p_pay_return_url) {
        this.p2p_pay_return_url = p2p_pay_return_url;
    }

    public String getNotify_url() {
        return notify_url;
    }

    public void setNotify_url(String notify_url) {
        this.notify_url = notify_url;
    }

    public String getReturn_url() {
        return return_url;
    }

    public void setReturn_url(String return_url) {
        this.return_url = return_url;
    }

    public String getAlipay_public_key() {
        return alipay_public_key;
    }

    public void setAlipay_public_key(String alipay_public_key) {
        this.alipay_public_key = alipay_public_key;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public String getApp_id() {
        return app_id;
    }

    public void setApp_id(String app_id) {
        this.app_id = app_id;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getGatewayUrl() {
        return gatewayUrl;
    }

    public void setGatewayUrl(String gatewayUrl) {
        this.gatewayUrl = gatewayUrl;
    }

    public String getSign_type() {
        return sign_type;
    }

    public void setSign_type(String sign_type) {
        this.sign_type = sign_type;
    }

    public String getMerchant_private_key() {
        return merchant_private_key;
    }

    public void setMerchant_private_key(String merchant_private_key) {
        this.merchant_private_key = merchant_private_key;
    }
}
