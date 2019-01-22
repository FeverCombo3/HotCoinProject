package com.yjq.hotcoin.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;
import com.wang.avi.AVLoadingIndicatorView;
import com.yjq.hotcoin.R;
import com.yjq.hotcoin.bean.Result;
import com.yjq.hotcoin.bean.TradeData;
import com.yjq.hotcoin.callback.JsonCallBack;
import com.yjq.hotcoin.util.DateUtil;
import com.yjq.hotcoin.util.NumberFormatUtil;
import com.yjq.hotcoin.view.WrapContentHeightViewPager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 成交
 */
public class TradeFragment extends Fragment {

    private WrapContentHeightViewPager vp;
    private int index;
    private AVLoadingIndicatorView mLoading;
    private RecyclerView recyclerView;
    private TradeAdapter adapter;

    private List<TradeData.Trade> mTrades = new ArrayList<>();


    public TradeFragment() {
        // Required empty public constructor
    }

    public void setViewPage(WrapContentHeightViewPager vp, int index){
        this.vp=vp;
        this.index=index;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_trade, container, false);

        initView(view);

        vp.setObjectForPosition(view,index);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getTradeData("btcusdt","20");
    }

    private void initView(View view){
        mLoading = view.findViewById(R.id.trade_loading);

        recyclerView = view.findViewById(R.id.rv_trade);
        adapter = new TradeAdapter(mTrades);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        recyclerView.setAdapter(adapter);
    }

    /**
     * 火币行情API(批量获取最近的交易记录)
     *
     * @param symbol 交易对 (btcusdt, bchbtc, rcneth ...)
     * @param size 交易记录数量 ([1, 2000])
     */
    private void getTradeData(String symbol,String size){
        OkGo.<Result<List<TradeData>>>get("https://api.huobipro.com/market/history/trade")
                .params("symbol",symbol)
                .params("size",size)
                .execute(new JsonCallBack<Result<List<TradeData>>>() {
                    @Override
                    public void onStart(Request<Result<List<TradeData>>, ? extends Request> request) {
                        super.onStart(request);
                        mLoading.show();
                    }

                    @Override
                    public void onSuccess(Response<Result<List<TradeData>>> response) {
                        List<TradeData> tradeData = response.body().data;
                        handleTradeData(tradeData);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        mLoading.hide();
                    }
                });
    }

    private void handleTradeData(List<TradeData> tradeData){
        for (TradeData data : tradeData){
            List<TradeData.Trade> trades = data.data;
            if(mTrades.size() < 20){
                mTrades.addAll(trades);
            }else {
                break;
            }
        }

        adapter.notifyDataSetChanged();
    }

    class TradeAdapter extends RecyclerView.Adapter<TradeAdapter.TradeHolder>{

        private List<TradeData.Trade> trades = new ArrayList<>();

        public TradeAdapter(List<TradeData.Trade> trades){
            this.trades = trades;
        }

        @NonNull
        @Override
        public TradeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.item_trade,parent,false);
            TradeHolder holder = new TradeHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull TradeHolder holder, int position) {
            holder.tradeTime.setText(DateUtil.millsToDate(trades.get(position).ts));
            holder.tradePrice.setText(NumberFormatUtil.getFourDecimal(trades.get(position).price));
            holder.tradeAmount.setText(NumberFormatUtil.getTwoDecimal(trades.get(position).amount));
            if(trades.get(position).direction.equals("buy")){
                holder.tradeDir.setText("买入");
                holder.tradeDir.setTextColor(getResources().getColor(R.color.kline_green));
            }else {
                holder.tradeDir.setText("卖出");
                holder.tradeDir.setTextColor(getResources().getColor(R.color.kline_red));
            }
        }

        @Override
        public int getItemCount() {
            return trades.size();
        }

        class TradeHolder extends RecyclerView.ViewHolder{

            public TextView tradeTime;
            public TextView tradeDir;
            public TextView tradePrice;
            public TextView tradeAmount;

            public TradeHolder(@NonNull View itemView) {
                super(itemView);

                tradeTime = itemView.findViewById(R.id.tv_trade_time);
                tradeDir = itemView.findViewById(R.id.tv_trade_dir);
                tradePrice = itemView.findViewById(R.id.tv_trade_price);
                tradeAmount = itemView.findViewById(R.id.tv_trade_amount);
            }
        }
    }
}
