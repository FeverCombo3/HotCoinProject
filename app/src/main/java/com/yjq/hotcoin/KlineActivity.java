package com.yjq.hotcoin;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;
import com.wang.avi.AVLoadingIndicatorView;
import com.yjq.hotcoin.bean.CandleData;
import com.yjq.hotcoin.bean.Result;
import com.yjq.hotcoin.bean.Result2;
import com.yjq.hotcoin.bean.Symbol;
import com.yjq.hotcoin.bean.TfTrade;
import com.yjq.hotcoin.callback.JsonCallBack;
import com.yjq.hotcoin.callback.JsonCallBack2;
import com.yjq.hotcoin.fragment.DeepFragment;
import com.yjq.hotcoin.fragment.IntroFragment;
import com.yjq.hotcoin.fragment.TradeFragment;
import com.yjq.hotcoin.indicator.Indicator;
import com.yjq.hotcoin.indicator.Kline;
import com.yjq.hotcoin.indicator.VOL;
import com.yjq.hotcoin.kline.KlineView;
import com.yjq.hotcoin.type.Cycle;
import com.yjq.hotcoin.util.IndicatorUtil;
import com.yjq.hotcoin.util.NumberFormatUtil;
import com.yjq.hotcoin.util.PreferenceUtil;
import com.yjq.hotcoin.util.UIHelper;
import com.yjq.hotcoin.view.KlineToolBar;
import com.yjq.hotcoin.view.WrapContentHeightViewPager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class KlineActivity extends AppCompatActivity implements KlineToolBar.OnKlineToolBarClickListener {
    public static final String PARAM_SYMBOL = "symbol";
    private static final int KLINE_REQUEST_CODE = 100;

    private static int KLINE_SIZE = 400;

    private KlineView mKlineView;
    private List<Indicator> indicators = new ArrayList<>();
    private List<Fragment> fragmentsList;
    private KlineToolBar toolBar;
    private TabLayout tabLayout;
    private WrapContentHeightViewPager viewPager;
    private ViewPager.OnPageChangeListener pageChangeListener;

    private TextView mNowPrice,mRate,mHighPrice,mLowPrice,m24Amount,mCnY;

    private AVLoadingIndicatorView mLoading;
    private List<CandleData> mData;

    private String symbol;
    private String title;

    private Timer timer;
    private boolean refresh = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initParams();

        initIndicators();

        initKlineView();

        initBottom();

        getHQdataSchedule();
    }

    private void initParams(){
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        if(intent != null){
            Symbol symbol = (Symbol) intent.getSerializableExtra(PARAM_SYMBOL);
            this.symbol = symbol.symbol;
            if(actionBar != null){
                title = symbol.base_currency.toUpperCase() + "/" + symbol.quote_currency.toUpperCase();
                actionBar.setTitle(title);
            }
        }
    }

    private void initKlineView() {
        mKlineView = findViewById(R.id.klineView);
        mNowPrice = findViewById(R.id.tv_now_price);
        mHighPrice = findViewById(R.id.tv_high_price);
        mRate = findViewById(R.id.tv_rate);
        mLowPrice = findViewById(R.id.tv_low_price);
        m24Amount = findViewById(R.id.tv_24h_vol);
        tabLayout = findViewById(R.id.kline_tab);
        viewPager = findViewById(R.id.kline_vp);
        toolBar = findViewById(R.id.kline_toolbar);
        mLoading = findViewById(R.id.kline_loading);
        mCnY = findViewById(R.id.tv_price_rmb);
        toolBar.setOnKlineToolBarClickListener(this);
        mKlineView.setLeftOrRightListener(new KlineView.LeftOrRightListener() {
            @Override
            public void left() {
                //Toast.makeText(KlineActivity.this, "没有数据了", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void right() {
               // Toast.makeText(KlineActivity.this, "已经是最新数据了", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void initIndicators() {
        indicators.clear();
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

    private void initBottom() {
        fragmentsList = new ArrayList<>();
        DeepFragment deepFragment = new DeepFragment();
        deepFragment.setViewPage(viewPager, 0);
        fragmentsList.add(deepFragment);

        TradeFragment tradeFragment = new TradeFragment();
        tradeFragment.setViewPage(viewPager, 1);
        fragmentsList.add(tradeFragment);

        IntroFragment introFragment = new IntroFragment();
        introFragment.setViewPage(viewPager, 2);
        fragmentsList.add(introFragment);

        KlinePagerAdapter adapter = new KlinePagerAdapter(getSupportFragmentManager());
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager);
        viewPager.resetHeight(0);

        pageChangeListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                viewPager.resetHeight(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };

        viewPager.addOnPageChangeListener(pageChangeListener);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                pageChangeListener.onPageSelected(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

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
                                    mNowPrice.setTextColor(getResources().getColor(R.color.kline_green));
                                    rate = "+" + rate;
                                }else if(Float.parseFloat(rate)  < 0){
                                    mRate.setTextColor(getResources().getColor(R.color.kline_red));
                                    mNowPrice.setTextColor(getResources().getColor(R.color.kline_red));
                                }else{
                                    mRate.setTextColor(getResources().getColor(R.color.color_7A7A7A_second_important_text_2));
                                    mNowPrice.setTextColor(getResources().getColor(R.color.color_7A7A7A_second_important_text_2));
                                }
                                mRate.setText(rate + "%");
                            }

                            mNowPrice.setText(mData.get(mData.size() - 1).close + "");
                            mCnY.setText("≈" + NumberFormatUtil.transToCny(mData.get(mData.size() - 1).close) + "CNY");

                            for (Indicator indicator : indicators) {
                                indicator.compute(mData);
                            }

                            mKlineView.setData(mData, indicators, refresh);
                        }
                    }

                    @Override
                    public void onError(Response<Result<List<CandleData>>> response) {
                        super.onError(response);
                        Toast.makeText(KlineActivity.this, "网络发生错误或需要翻墙！", Toast.LENGTH_SHORT).show();
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
                            mHighPrice.setText(tfTrade.high + "");
                            mLowPrice.setText(tfTrade.low + "");
                            m24Amount.setText(tfTrade.vol + "");
                        }
                    }
                });
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
        mKlineView.setData(mData, indicators, false);
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
        mKlineView.setData(mData, indicators, false);
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
        mKlineView.setData(mData, indicators, false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }

    @Override
    public void onCycleClick(Cycle cycle) {
        if(cycle != Cycle.CYCLE_FENSHI){
            refresh = true;
            timer.cancel();
            getHQdataSchedule();
        }else {
            Toast.makeText(this,"Api没有分时数据！",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onIndicatorClick(Indicator indicator) {
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

    class KlinePagerAdapter extends FragmentPagerAdapter {

        private String[] mTitles = new String[]{"深度", "成交", "简介"};

        public KlinePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentsList.get(position);
        }

        @Override
        public int getCount() {
            return mTitles.length;
        }

        //ViewPager与TabLayout绑定后，这里获取到PageTitle就是Tab的Text
        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position];
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_kline,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.expand:
                Intent intent = new Intent(this,KlineHActivity.class);
                intent.putExtra(KlineHActivity.PARAM_SYMBOL,symbol);
                intent.putExtra(KlineHActivity.PARAM_TITLE,title);
                startActivityForResult(intent,KLINE_REQUEST_CODE);
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == KLINE_REQUEST_CODE && resultCode == RESULT_OK){

            initIndicators();
            if(mData == null){
                return;
            }
            for (Indicator i2 : indicators) {
                i2.compute(mData);
            }
            mKlineView.setData(mData, indicators, false);
        }
    }
}
