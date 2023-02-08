package com.yjq.hotcoin.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yjq.hotcoin.R;
import com.yjq.hotcoin.indicator.BOLL;
import com.yjq.hotcoin.indicator.base.Indicator;
import com.yjq.hotcoin.indicator.KDJ;
import com.yjq.hotcoin.indicator.MA;
import com.yjq.hotcoin.indicator.MACD;
import com.yjq.hotcoin.indicator.RSI;
import com.yjq.hotcoin.indicator.ZLCP;
import com.yjq.hotcoin.util.PreferenceUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yjq
 * 2018/6/20.
 * 横屏数值指标选择
 */

public class IndicatorVerticalBar extends LinearLayout implements View.OnClickListener{
    private List<Indicator> mainIndicators = new ArrayList<>();
    private List<Indicator> subIndicators = new ArrayList<>();
    private List<TextView> mainViews = new ArrayList<>();
    private List<TextView> subViews = new ArrayList<>();
    private Context mContext;

    private TextView mMainHide,mSubHide;

    private OnHIndicatorClickListener mListener;

    private boolean change = false; //是否切换过指标或周期

    public IndicatorVerticalBar(Context context) {
        super(context);
        init(context);
    }

    public IndicatorVerticalBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public IndicatorVerticalBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        setOrientation(VERTICAL);
        mContext = context;
        setBackgroundResource(R.drawable.bg_indicator_bar);

        mainIndicators.add(new MA(mContext));
        mainIndicators.add(new BOLL(mContext));

        subIndicators.add(new MACD(mContext));
        subIndicators.add(new KDJ(mContext));
        subIndicators.add(new RSI(mContext));
        subIndicators.add(new ZLCP(mContext));

        TextView mainT = new TextView(mContext);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,0);
        params.weight = 1;
        mainT.setGravity(Gravity.CENTER);
        mainT.setText("主图");
        mainT.setTextSize(13);
        mainT.setTextColor(mContext.getResources().getColor(R.color.kline_blue_choose));
        mainT.setLayoutParams(params);
        addView(mainT);

        int mainIndex = PreferenceUtil.getInstance().getMainIndicator();

        for (final Indicator indicator : mainIndicators){
            TextView tv = new TextView(mContext);
            tv.setTextSize(13);
            tv.setGravity(Gravity.CENTER);
            tv.setText(indicator.getName());
            tv.setId(indicator.getIndex());
            if(mainIndex == indicator.getIndex()){
                tv.setTextColor(mContext.getResources().getColor(R.color.color_white));
            }else {
                tv.setTextColor(mContext.getResources().getColor(R.color.kline_blue));
            }
            tv.setLayoutParams(params);
            tv.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleIndicatorClick(v,indicator);
                }
            });
            mainViews.add(tv);
            addView(tv);
        }

        mMainHide = new TextView(mContext);
        mMainHide.setText("隐藏");
        mMainHide.setTextSize(13);
        if(mainIndex == -1){
            mMainHide.setTextColor(mContext.getResources().getColor(R.color.color_white));
        }else {
            mMainHide.setTextColor(mContext.getResources().getColor(R.color.kline_blue_choose));
        }
        mMainHide.setGravity(Gravity.CENTER);
        mMainHide.setLayoutParams(params);
        mMainHide.setOnClickListener(this);
        addView(mMainHide);

        View divider = new View(mContext);
        divider.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,1));
        divider.setBackgroundColor(mContext.getResources().getColor(R.color.kline_blue));
        addView(divider);

        TextView subT = new TextView(mContext);
        subT.setText("副图");
        subT.setTextSize(13);
        subT.setTextColor(mContext.getResources().getColor(R.color.kline_blue_choose));
        subT.setGravity(Gravity.CENTER);
        subT.setLayoutParams(params);
        addView(subT);

        int subIndex = PreferenceUtil.getInstance().getSubIndicator();

        for (final Indicator indicator : subIndicators){
            TextView tv = new TextView(mContext);
            tv.setTextSize(13);
            tv.setTextColor(mContext.getResources().getColor(R.color.kline_blue));
            tv.setText(indicator.getName());
            tv.setId(indicator.getIndex());
            if(subIndex == indicator.getIndex()){
                tv.setTextColor(mContext.getResources().getColor(R.color.color_white));
            }else {
                tv.setTextColor(mContext.getResources().getColor(R.color.kline_blue));
            }
            tv.setGravity(Gravity.CENTER);
            tv.setLayoutParams(params);
            tv.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleIndicatorClick(v,indicator);
                }
            });
            subViews.add(tv);
            addView(tv);
        }

        mSubHide = new TextView(mContext);
        mSubHide.setText("隐藏");
        mSubHide.setTextSize(13);
        if(subIndex == -1){
            mSubHide.setTextColor(mContext.getResources().getColor(R.color.color_white));
        }else {
            mSubHide.setTextColor(mContext.getResources().getColor(R.color.kline_blue_choose));
        }
        mSubHide.setGravity(Gravity.CENTER);
        mSubHide.setLayoutParams(params);
        mSubHide.setOnClickListener(this);
        addView(mSubHide);
    }

    @Override
    public void onClick(View v) {
        if(v == mMainHide){
            handleMainHide();
        }else if(v == mSubHide){
            handleSubHide();
        }
    }

    private void handleMainHide(){
        PreferenceUtil.getInstance().setMainIndicator(-1);
        clearMainViews();
        mMainHide.setTextColor(mContext.getResources().getColor(R.color.color_white));
        if(mListener != null){
            mListener.onMainIndicatorHide();
        }
    }

    private void handleSubHide(){
        PreferenceUtil.getInstance().setSubIndicator(-1);
        PreferenceUtil.getInstance().setKlineType(0);
        clearSubViews();
        mSubHide.setTextColor(mContext.getResources().getColor(R.color.color_white));
        if(mListener != null){
            mListener.onSubIndicatorHide();
        }
    }

    private void handleIndicatorClick(View view,Indicator indicator){
        if (isMainIndicator(view.getId())){//点击的是主图指标
            for (TextView textView : mainViews){
                if(textView.getId() == view.getId()){
                    textView.setTextColor(mContext.getResources().getColor(R.color.color_white));
                }else {
                    textView.setTextColor(mContext.getResources().getColor(R.color.kline_blue));
                }
            }
            mMainHide.setTextColor(mContext.getResources().getColor(R.color.kline_blue));
            PreferenceUtil.getInstance().setMainIndicator(view.getId());
        }else {//点击的是副图指标
            for (TextView textView : subViews){
                if(textView.getId() == view.getId()){
                    textView.setTextColor(mContext.getResources().getColor(R.color.color_white));
                }else {
                    textView.setTextColor(mContext.getResources().getColor(R.color.kline_blue));
                }
            }
            mSubHide.setTextColor(mContext.getResources().getColor(R.color.kline_blue));
            PreferenceUtil.getInstance().setSubIndicator(view.getId());
            PreferenceUtil.getInstance().setKlineType(1);
        }
        if(mListener != null){
            mListener.onHIndicatorClick(indicator);
        }
    }

    private void clearMainViews(){
        for (TextView textView : mainViews){
            textView.setTextColor(mContext.getResources().getColor(R.color.kline_blue));
        }
    }

    private void clearSubViews(){
        for (TextView textView : subViews){
            textView.setTextColor(mContext.getResources().getColor(R.color.kline_blue));
        }
    }

    private boolean isMainIndicator(int index){
        for (Indicator indicator : mainIndicators){
            if(indicator.getIndex() == index){
                return true;
            }
        }
        return false;
    }

    public interface OnHIndicatorClickListener{

        void onHIndicatorClick(Indicator indicator);

        //隐藏主图指标
        void onMainIndicatorHide();

        //隐藏副图指标
        void onSubIndicatorHide();
    }

    public void setOnHIndicatorClickListener(OnHIndicatorClickListener listener){
        this.mListener = listener;
    }
}
