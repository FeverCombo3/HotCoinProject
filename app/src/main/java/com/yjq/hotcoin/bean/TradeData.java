package com.yjq.hotcoin.bean;

import java.math.BigInteger;
import java.util.List;

/**
 * Created by yjq on 2018/5/24.
 */

public class TradeData {

    public long id;
    public long ts;
    public List<Trade> data;

    public class Trade{
        public double amount;
        public long ts;
        public BigInteger id;
        public double price;
        public String direction;
    }
}
