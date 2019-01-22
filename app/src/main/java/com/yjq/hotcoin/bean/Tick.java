package com.yjq.hotcoin.bean;

import java.util.List;

/**
 * Created by yjq on 2018/5/23.
 */

public class Tick {
    public List<List<Double>> bids;
    public List<List<Double>> asks;
    public long ts;
    public long version;
}
