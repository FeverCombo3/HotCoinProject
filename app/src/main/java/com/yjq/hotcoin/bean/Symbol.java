package com.yjq.hotcoin.bean;

import java.io.Serializable;

/**
 * Created by yjq
 * 2018/6/1.
 * 交易对
 */

public class Symbol implements Serializable{

    public String base_currency;
    public String quote_currency;
    public String price_precision;
    public String amount_precision;
    public String symbol;
}
