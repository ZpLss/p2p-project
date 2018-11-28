package com.bjpowernode.p2p.service.loan;

/**
 * ClassName:OnlyNumberService
 * Package:com.bjpowernode.p2p.service.loan
 * Description:
 * Date:2018/3/20 15:39
 * Author:13651027050
 */
public interface OnlyNumberService {

    /**
     * 获取redis的唯一数字
     * @return
     */
    Long getOnlyNumber();
}
