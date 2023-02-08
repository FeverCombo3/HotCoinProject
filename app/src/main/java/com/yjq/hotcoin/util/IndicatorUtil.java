package com.yjq.hotcoin.util;

import android.content.Context;

import com.yjq.hotcoin.indicator.BOLL;
import com.yjq.hotcoin.indicator.base.Indicator;
import com.yjq.hotcoin.indicator.KDJ;
import com.yjq.hotcoin.indicator.Kline;
import com.yjq.hotcoin.indicator.MA;
import com.yjq.hotcoin.indicator.MACD;
import com.yjq.hotcoin.indicator.RSI;
import com.yjq.hotcoin.indicator.VOL;
import com.yjq.hotcoin.indicator.ZLCP;

/**
 * Created by yjq
 * 2018/6/19.
 */

public class IndicatorUtil {

    public static Indicator newIndicatorInstance(int index, Context context){
        switch (index){
            case Indicator.INDICATOR_INDEX_K:
                return new Kline(context);
            case Indicator.INDICATOR_INDEX_VOL:
                return new VOL(context);
            case Indicator.INDICATOR_INDEX_KDJ:
                return new KDJ(context);
            case Indicator.INDICATOR_INDEX_MACD:
                return new MACD(context);
            case Indicator.INDICATOR_INDEX_RSI:
                return new RSI(context);
            case Indicator.INDICATOR_INDEX_MA:
                return new MA(context);
            case Indicator.INDICATOR_INDEX_BOLL:
                return new BOLL(context);
            case Indicator.INDICATOR_INDEX_ZLCP:
                return new ZLCP(context);
        }
        return new MACD(context);
    }
}
