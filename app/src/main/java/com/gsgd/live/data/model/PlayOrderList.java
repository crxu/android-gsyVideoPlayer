package com.gsgd.live.data.model;

import com.gsgd.live.data.response.RespPlayOrder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andy on 2017/12/25.
 */

public class PlayOrderList {

    private List<PlayOrder> orders = new ArrayList<PlayOrder>();

    public List<PlayOrder> getOrders() {
        return orders;
    }

    public void addOrder(PlayOrder order) {
        this.orders.add(order);
    }

}
