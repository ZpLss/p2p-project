package com.bjpowernode.p2p.model.vo;

import java.io.Serializable;

/**
 * ClassName:UserTop
 * Package:com.bjpowernode.p2p.model.vo
 * Description:
 * Date:2018/3/15 20:02
 * Author:13651027050
 */
public class UserTop implements Serializable {
    /**
     * 手机号
     *
     */
    private String phone;
    /**
     * 用户投资金额
     *
     */
    private Double score;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }
}
