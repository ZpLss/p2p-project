package com.bjpowernode.p2p.model.vo;


import java.io.Serializable;
import java.util.List;

/**
 * ClassName:PaginationVO
 * Package:com.bjpowernode.p2p.model.vo
 * Description:
 * Date:2018/3/10 19:24
 * Author:13651027050
 */
public class PaginationVO<T> implements Serializable {

    /**
     * 总数据量
     */
    private Long total;

    /**
     * 总数据
     */
    private List<T> dataList;

    public List<T> getDataList() {

        return dataList;
    }

    public void setDataList(List<T> dataList) {

        this.dataList = dataList;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }
}
