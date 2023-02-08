package com.yjq.hotcoin.util;

import android.content.Context;

import com.yjq.hotcoin.MyApp;
import com.yjq.hotcoin.indicator.base.Indicator;

/**
 * Created by yjq
 * 2018/6/19.
 */

public class PreferenceUtil extends BasePreference{
    private static PreferenceUtil preferenceUtils;

    public static String KLINE_INDICATOR_HIDE = "indicator_hide";

    /**
     * 需要增加key就在这里新建
     */
    //Kline的周期
    private String KLINE_CYCLE = "kline_cycle";
    //Kline的主图指标
    private String KLINE_MAIN_INDICATOR = "main_indicator";
    //Kline的副图指标
    private String KLINE_SUB_INDICATOR = "sub_indicator";
    //KlineTYpe
    private String KLINE_TYPE = "kline_type";



    private PreferenceUtil(Context context) {
        super(context);
    }

    /**
     * 这里我通过自定义的Application来获取Context对象，所以在获取preferenceUtils时不需要传入Context。
     * @return
     */
    public synchronized static PreferenceUtil getInstance() {
        if (null == preferenceUtils) {
            preferenceUtils = new PreferenceUtil(MyApp.getContext());
        }
        return preferenceUtils;
    }


    public void setKlineCycle(int cycle){
        setInt(KLINE_CYCLE,cycle);
    }

    public int getKlineCycle(){
        return getInt(KLINE_CYCLE);
    }

    public void setMainIndicator(int index){
        setInt(KLINE_MAIN_INDICATOR,index);
    }

    public int getMainIndicator(){
        return getInt(KLINE_MAIN_INDICATOR,Indicator.INDICATOR_INDEX_MA);
    }

    public void setSubIndicator(int index){
        setInt(KLINE_SUB_INDICATOR,index);
    }

    public int getSubIndicator(){
        return getInt(KLINE_SUB_INDICATOR, Indicator.INDICATOR_INDEX_MACD);
    }

    public void setKlineType(int type){
        setInt(KLINE_TYPE,type);
    }

    public int getKlineType(){
        return getInt(KLINE_TYPE, 1);
    }
}
