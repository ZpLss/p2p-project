package com.bjpowernode.p2p.model.vo;

import java.io.Serializable;

/**
 * ClassName:ResultObject
 * Package:com.bjpowernode.p2p.model.vo
 * Description:
 * Date:2018/3/11 16:30
 * Author:13651027050
 */
public class ResultObject implements Serializable {
    /**
     * 错误码:success成功,fail失败
     */
    private String errorCode;

    /**
     * 错误消息
     */
    private String errorMessage;

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
