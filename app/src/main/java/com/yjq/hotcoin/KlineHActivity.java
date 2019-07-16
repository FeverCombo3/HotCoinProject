package com.yjq.hotcoin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;
import com.wang.avi.AVLoadingIndicatorView;
import com.yjq.hotcoin.bean.CandleData;
import com.yjq.hotcoin.bean.Result;
import com.yjq.hotcoin.bean.Result2;
import com.yjq.hotcoin.bean.TfTrade;
import com.yjq.hotcoin.callback.JsonCallBack;
import com.yjq.hotcoin.callback.JsonCallBack2;
import com.yjq.hotcoin.indicator.Indicator;
import com.yjq.hotcoin.indicator.Kline;
import com.yjq.hotcoin.indicator.VOL;
import com.yjq.hotcoin.kline.KlineView;
import com.yjq.hotcoin.type.Cycle;
import com.yjq.hotcoin.util.IndicatorUtil;
import com.yjq.hotcoin.util.PreferenceUtil;
import com.yjq.hotcoin.util.UIHelper;
import com.yjq.hotcoin.view.CycleLandscapeBar;
import com.yjq.hotcoin.view.IndicatorVerticalBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class KlineHActivity extends AppCompatActivity implements CycleLandscapeBar.OnHCycleClickListener,IndicatorVerticalBar.OnHIndicatorClickListener{
    public static final String PARAM_SYMBOL = "symbol";
    public static final String PARAM_TITLE = "title";

    private static int KLINE_SIZE = 400;

    private AVLoadingIndicatorView mLoading;
    private KlineView mKLineView;
    private List<CandleData> mData;
    private List<Indicator> indicators = new ArrayList<>();

    private String symbol;

    private TextView mTvSymbol,mTvHPrice,mRate;
    private TextView mTv24Vol,mTv24High,mTv24Low;
    private ImageView mClose;
    private CycleLandscapeBar landscapeBar;
    private IndicatorVerticalBar verticalBar;

    private Timer timer;
    private boolean refresh = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kline_h);

        initView();

        initParams();

        initIndicators();

        getHQdataSchedule();
    }

    private void initParams(){
        Intent intent = getIntent();
        if(intent != null){
            symbol = intent.getStringExtra(PARAM_SYMBOL);
            String title = intent.getStringExtra(PARAM_TITLE);
            mTvSymbol.setText(title);
        }
    }

    private void initView(){
        mLoading = findViewById(R.id.kline_loading);
        mKLineView = findViewById(R.id.klineView);
        mTvSymbol = findViewById(R.id.tv_h_symbol);
        mTvHPrice = findViewById(R.id.tv_h_price);
        mTv24Vol = findViewById(R.id.tv_h_24vol);
        mTv24Low = findViewById(R.id.tv_h_low);
        mRate = findViewById(R.id.tv_h_rate);
        mTv24High = findViewById(R.id.tv_h_high);
        landscapeBar = findViewById(R.id.land_bar);
        landscapeBar.setOnHCycleClickListener(this);
        verticalBar = findViewById(R.id.vertical_bar);
        verticalBar.setOnHIndicatorClickListener(this);
        mClose = findViewById(R.id.iv_h_close);
        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult();
            }
        });
    }

    private void initIndicators() {
        //k线绑定
        indicators.add(new Kline(this));
        //主图指标
        indicators.add(IndicatorUtil.newIndicatorInstance(PreferenceUtil.getInstance().getMainIndicator(),this));
        //vol是固定的
        indicators.add(new VOL(this));
        //副图指标
        if(PreferenceUtil.getInstance().getKlineType() == 1){
            int index = PreferenceUtil.getInstance().getSubIndicator();
            indicators.add(IndicatorUtil.newIndicatorInstance(index,this));
        }
    }


    /**
     * 火币行情API(获取K线数据)
     *
     * @param symbol 交易对 (btcusdt, bchbtc, rcneth ...)
     * @param period K线类型 (1min, 5min, 15min, 30min, 60min, 1day, 1mon, 1week, 1year)
     * @param size   获取数量
     */
    private void getHuoCoinKlineData(String symbol, String period, String size) {
        OkGo.<Result<List<CandleData>>>get("https://api.huobi.pro/market/history/kline")
                .tag(this)
                .params("period", period)
                .params("size", size)
                .params("symbol", symbol)
                .params("AccessKeyId", "fff-xxx-ssss-kkk")
                .execute(new JsonCallBack<Result<List<CandleData>>>() {

                    @Override
                    public void onStart(Request<Result<List<CandleData>>, ? extends Request> request) {
                        super.onStart(request);
                        if(refresh){
                            mLoading.setVisibility(View.VISIBLE);
                            mLoading.show();
                        }
                    }

                    @Override
                    public void onSuccess(Response<Result<List<CandleData>>> response) {
                        mData = response.body().data;
                        if (mData != null && mData.size() != 0) {
                            Collections.reverse(mData);
                            if(mData.size() >= 2){
                                String rate = UIHelper.getRateValue(mData.get(mData.size() - 1).close,mData.get(mData.size() - 2).close);
                                if(Float.parseFloat(rate) > 0){
                                    mRate.setTextColor(getResources().getColor(R.color.kline_green));
                                    mTvHPrice.setTextColor(getResources().getColor(R.color.kline_green));
                                    rate = "+" + rate;
                                }else if(Float.parseFloat(rate)  < 0){
                                    mRate.setTextColor(getResources().getColor(R.color.kline_red));
                                    mTvHPrice.setTextColor(getResources().getColor(R.color.kline_red));
                                }else{
                                    mRate.setTextColor(getResources().getColor(R.color.color_7A7A7A_second_important_text_2));
                                    mTvHPrice.setTextColor(getResources().getColor(R.color.color_7A7A7A_second_important_text_2));
                                }
                                mRate.setText(rate + "%");
                            }

                            mTvHPrice.setText(mData.get(mData.size() - 1).close + "");
                            for (Indicator indicator : indicators) {
                                indicator.compute(mData);
                            }
                            mKLineView.setData(mData, indicators, refresh);
                        }
                    }

                    @Override
                    public void onError(Response<Result<List<CandleData>>> response) {
                        super.onError(response);
                        Toast.makeText(KlineHActivity.this, "网络发生错误或需要翻墙！", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        mLoading.hide();
                        refresh = false;
                    }
                });
    }

    /**
     * 火币行情API(获取24小时交易详情)
     *
     * @param symbol 交易对 (btcusdt, bchbtc, rcneth ...)
     */
    private void getHuoCoin24Trade(String symbol){
        OkGo.<Result2<TfTrade>>get("https://api.huobipro.com/market/detail")
                .params("symbol",symbol)
                .execute(new JsonCallBack2<Result2<TfTrade>>() {
                    @Override
                    public void onSuccess(Response<Result2<TfTrade>> response) {
                        TfTrade tfTrade = response.body().tick;
                        if(tfTrade != null){
                            mTv24High.setText(tfTrade.high + "");
                            mTv24Low.setText(tfTrade.low + "");
                            mTv24Vol.setText(tfTrade.vol + "");
                        }
                    }
                });
    }

    /**
     * 每5s主动拉行情数据
     */
    private void getHQdataSchedule(){
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                getHuoCoinKlineData(symbol, Cycle.getCycleFromIndex(PreferenceUtil.getInstance().getKlineCycle()).getValue(), String.valueOf(KLINE_SIZE));
                getHuoCoin24Trade(symbol);
            }
        }, 0, 5000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }

    @Override
    public void onHCycleClick(Cycle cycle) {
        if(cycle != Cycle.CYCLE_FENSHI){
            refresh = true;
            timer.cancel();
            getHQdataSchedule();
        }else {
            Toast.makeText(this,"Api没有分时数据！",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onHIndicatorClick(Indicator indicator) {
        changeIndicator(indicator);
    }

    @Override
    public void onMainIndicatorHide() {
        hideMainIndicator();
    }

    @Override
    public void onSubIndicatorHide() {
        hideSubIndicator();
    }

    private void changeIndicator(Indicator indicator){
        for (int i=0;i<indicators.size();i++){
            if(indicators.get(i).getposition() == indicator.getposition()){
                indicators.remove(i);
                indicators.add(indicator);
                break;
            }
            if(i == indicators.size() - 1){
                indicators.add(indicator);
            }
        }
        if(mData == null){
            return;
        }
        for (Indicator i2 : indicators) {
            i2.compute(mData);
        }
        mKLineView.setData(mData, indicators, false);
    }

    private void hideMainIndicator(){
        for (Indicator i : indicators){
            if(i.getposition() == 1){
                indicators.remove(i);
                break;
            }
        }
        if(mData == null){
            return;
        }
        for (Indicator i2 : indicators) {
            i2.compute(mData);
        }
        mKLineView.setData(mData, indicators, false);
    }

    private void hideSubIndicator(){
        for (Indicator i : indicators){
            if(i.getposition() == 3){
                indicators.remove(i);
                break;
            }
        }
        if(mData == null){
            return;
        }
        for (Indicator i2 : indicators) {
            i2.compute(mData);
        }
        mKLineView.setData(mData, indicators, false);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            setResult();
            return false;
        }else {
            return super.onKeyDown(keyCode, event);
        }
    }

    private void setResult(){
        setResult(RESULT_OK);
        finish();
    }
}
