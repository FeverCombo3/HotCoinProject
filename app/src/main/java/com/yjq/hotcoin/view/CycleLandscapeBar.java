package com.yjq.hotcoin.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.annotation.PluralsRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yjq.hotcoin.R;
import com.yjq.hotcoin.type.Cycle;
import com.yjq.hotcoin.util.DisplayUtils;
import com.yjq.hotcoin.util.PreferenceUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yjq
 * 2018/6/20.
 * 横屏底部周期选择
 */

public class CycleLandscapeBar extends LinearLayout implements View.OnClickListener{
    private Context mContext;

    private List<Cycle> cycleData = new ArrayList<>();
    private List<Cycle> cycleMoreData = new ArrayList<>();

    private View funcMore;

    private LinearLayout mMoreContainer,mBotCycleContainer;

    private OnHCycleClickListener mListener;

    public CycleLandscapeBar(Context context) {
        super(context);
        init(context);
    }

    public CycleLandscapeBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CycleLandscapeBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        mContext = context;
        setOrientation(VERTICAL);

        initCycleData();

        mMoreContainer = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.layout_kline_h_toolbar_more,null);
        mMoreContainer.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,DisplayUtils.dipToPixel(50)));

        addView(mMoreContainer);
        mMoreContainer.setVisibility(GONE);

        mBotCycleContainer  = new LinearLayout(mContext);
        mBotCycleContainer.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, DisplayUtils.dipToPixel(40)));
        mBotCycleContainer.setBackgroundColor(mContext.getResources().getColor(R.color.dark_bg));

        addView(mBotCycleContainer);

        int index = PreferenceUtil.getInstance().getKlineCycle();

        for (int i =0;i<cycleData.size();i++){
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_kline_cycle,null);
            view.setId(cycleData.get(i).getIndex());
            TextView cycle = view.findViewById(R.id.tv_cycle);
            View indicator = view.findViewById(R.id.indicator_cycle);
            cycle.setText(cycleData.get(i).getName());
            cycle.setTextSize(13);
            LayoutParams params = new LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
            params.weight = 1;
            view.setLayoutParams(params);
            if(isCycleIndex(index) && index == cycleData.get(i).getIndex()){
                indicator.setVisibility(VISIBLE);
                cycle.setTextColor(mContext.getResources().getColor(R.color.kline_blue_choose));
            }else {
                indicator.setVisibility(GONE);
                cycle.setTextColor(mContext.getResources().getColor(R.color.kline_blue));
            }
            view.setOnClickListener(this);
            mBotCycleContainer.addView(view);
        }

        View view = LayoutInflater.from(mContext).inflate(R.layout.item_kline_func,null);
        TextView func = view.findViewById(R.id.tv_func);
        func.setText("更多");
        func.setTextSize(13);
        funcMore = view;
        LayoutParams params = new LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        params.weight = 1;
        view.setLayoutParams(params);
        if(!isCycleIndex(index)){
            funcMoreCheck(Cycle.getCycleFromIndex(index).getName());
        }
        view.setOnClickListener(this);
        mBotCycleContainer.addView(view);
    }

    private void initCycleData(){
        cycleData.add(Cycle.CYCLE_FENSHI);
        cycleData.add(Cycle.CYCLE_15MIN);
        cycleData.add(Cycle.CYCLE_30MIN);
        cycleData.add(Cycle.CYCLE_1H);
        cycleData.add(Cycle.CYCLE_1DAY);
        cycleData.add(Cycle.CYCLE_1WEEK);
        cycleData.add(Cycle.CYCLE_1MON);
        cycleData.add(Cycle.CYCLE_1YEAR);
        cycleMoreData.add(Cycle.CYCLE_5MIN);
    }

    private boolean isCycleIndex(int index){
        for (Cycle cycle : cycleData){
            if(cycle.getIndex() == index){
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //为什么要延时呢？
        int childCount = mMoreContainer.getChildCount();
        for (int i=0;i<childCount;i++){
            View view = mMoreContainer.getChildAt(i);
            view.setId(cycleMoreData.get(i).getIndex());
            view.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        if(v == funcMore){
            handleMoreClick();
        }else if(isCycleIndex(v.getId())){
            handleCycleClick(v);
        }else {
            handleMoreCycleClick(v);
        }
    }

    private void handleCycleClick(View view){
        int count = mBotCycleContainer.getChildCount() - 1;
        for (int i=0;i<count;i++){
            View v = mBotCycleContainer.getChildAt(i);
            TextView cycle = v.findViewById(R.id.tv_cycle);
            View indicator = v.findViewById(R.id.indicator_cycle);
            if(v.getId() == view.getId()){
                cycle.setTextColor(mContext.getResources().getColor(R.color.kline_blue_choose));
                indicator.setVisibility(VISIBLE);
            }else {
                cycle.setTextColor(mContext.getResources().getColor(R.color.kline_blue));
                indicator.setVisibility(GONE);
            }
        }
        funcMoreUnCheck();
        if(mListener != null){
            mListener.onHCycleClick(Cycle.getCycleFromIndex(view.getId()));
        }
    }

    private void handleMoreCycleClick(View view){
        int count = mMoreContainer.getChildCount();
        for (int i=0 ;i<count;i++){
            View v = mMoreContainer.getChildAt(i);
            if(view.getId() == v.getId()){
                funcMoreCheck(cycleMoreData.get(i).getName());
                if(mListener != null){
                    mListener.onHCycleClick(Cycle.getCycleFromIndex(v.getId()));
                }
                break;
            }
        }
        funcMore.setBackgroundColor(mContext.getResources().getColor(R.color.dark_bg));
        mMoreContainer.setVisibility(GONE);
        clearCycle();
    }

    private void handleMoreClick(){
        if(mMoreContainer.getVisibility() == GONE){
            mMoreContainer.setVisibility(VISIBLE);
            funcMore.setBackgroundColor(mContext.getResources().getColor(R.color.color_black));
        }else {
            mMoreContainer.setVisibility(GONE);
            funcMore.setBackgroundColor(mContext.getResources().getColor(R.color.dark_bg));
        }
    }

    private void clearCycle(){
        int count = mBotCycleContainer.getChildCount() - 1;
        for (int i=0;i<count;i++){
            View v = mBotCycleContainer.getChildAt(i);
            TextView cycle = v.findViewById(R.id.tv_cycle);
            View indicator = v.findViewById(R.id.indicator_cycle);
            cycle.setTextColor(mContext.getResources().getColor(R.color.kline_blue));
            indicator.setVisibility(GONE);
        }
    }

    private void funcMoreCheck(String name){
        TextView more = funcMore.findViewById(R.id.tv_func);
        View indicator = funcMore.findViewById(R.id.indicator_func);
        more.setText(name);
        more.setTextColor(mContext.getResources().getColor(R.color.kline_blue_choose));
        indicator.setVisibility(VISIBLE);
    }

    private void funcMoreUnCheck(){
        TextView more = funcMore.findViewById(R.id.tv_func);
        View indicator = funcMore.findViewById(R.id.indicator_func);
        more.setText("更多");
        more.setTextColor(mContext.getResources().getColor(R.color.kline_blue));
        indicator.setVisibility(GONE);
    }

    public interface OnHCycleClickListener{

        void onHCycleClick(Cycle cycle);
    }

    public void setOnHCycleClickListener(OnHCycleClickListener listener){
        this.mListener = listener;
    }
}
