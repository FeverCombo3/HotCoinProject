package com.yjq.hotcoin.bean;

import java.util.List;

/**
 * Created by yjq on 2018/5/21.
 */

public class Result<T> {
    public String status;
    public String ch;
    public long ts;
    public T data;
}
