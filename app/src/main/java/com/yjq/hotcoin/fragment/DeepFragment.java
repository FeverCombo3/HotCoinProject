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

import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.yjq.hotcoin.callback.JsonCallBack;
import com.yjq.hotcoin.R;
import com.yjq.hotcoin.bean.Result2;
import com.yjq.hotcoin.bean.Tick;
import com.yjq.hotcoin.callback.JsonCallBack2;
import com.yjq.hotcoin.util.NumberFormatUtil;
import com.yjq.hotcoin.view.WrapContentHeightViewPager;

import java.nio.MappedByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * 深度
 */
public class DeepFragment extends Fragment {
    private WrapContentHeightViewPager vp;
    private int index;

    private RecyclerView recyclerView;
    private DeepAdapter adapter;

    private List<List<Double>> mBids = new ArrayList<>();
    private List<List<Double>> mAsks = new ArrayList<>();

    public DeepFragment() {
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
        View view = inflater.inflate(R.layout.fragment_deep, container, false);

        if(vp != null){
            vp.setObjectForPosition(view,index);
        }

        initView(view);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDeepData("btcusdt","step1");
    }

    private void initView(View view){
        recyclerView = view.findViewById(R.id.rv_deep);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new DeepAdapter(mBids,mAsks);
        recyclerView.setAdapter(adapter);
    }

    /**
     * 火币行情API(获取市场深度数据)
     *
     * @param symbol 交易对 (btcusdt, bchbtc, rcneth ...)
     * @param type Depth类型 (step0, step1, step2, step3, step4, step5（合并深度0-5）；step0时，不合并深度)
     */
    private void getDeepData(String symbol,String type){
        OkGo.<Result2<Tick>>get("https://api.huobipro.com/market/depth")
                .params("symbol",symbol)
                .params("type",type)
                .execute(new JsonCallBack2<Result2<Tick>>() {
                    @Override
                    public void onSuccess(Response<Result2<Tick>> response) {
                        Tick tick = response.body().tick;
                        mBids.clear();
                        mAsks.clear();
                        mBids.addAll(tick.bids);
                        mAsks.addAll(tick.asks);
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    class DeepAdapter extends RecyclerView.Adapter<DeepAdapter.DeepHolder>{
        private List<List<Double>> bids = new ArrayList<>();
        private List<List<Double>> asks = new ArrayList<>();

        public DeepAdapter(List<List<Double>> bids,List<List<Double>> asks){
            this.bids = bids;
            this.asks = asks;
        }

        @NonNull
        @Override
        public DeepHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.item_deep,parent,false);
            DeepHolder holder = new DeepHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull DeepHolder holder, int position) {
            double bid = bids.get(position).get(0);
            double bidamount = bids.get(position).get(1);

            double ask = asks.get(position).get(0);
            double askamount = asks.get(position).get(1);

            holder.numLeft.setText((position + 1) + "");
            holder.numRight.setText((position + 1) + "");
            holder.pricebuy.setText(NumberFormatUtil.getFourDecimal(bid));
            holder.amountbuy.setText(NumberFormatUtil.getFourDecimal(bidamount));
            holder.pricesell.setText(NumberFormatUtil.getFourDecimal(ask));
            holder.amountsell.setText(NumberFormatUtil.getFourDecimal(askamount));
        }

        @Override
        public int getItemCount() {
            return bids.size();
        }

        class DeepHolder extends RecyclerView.ViewHolder{
            public TextView numLeft;
            public TextView amountbuy;
            public TextView pricebuy;
            public TextView numRight;
            public TextView amountsell;
            public TextView pricesell;

            public DeepHolder(@NonNull View itemView) {
                super(itemView);
                numLeft = itemView.findViewById(R.id.tv_num_left);
                amountbuy = itemView.findViewById(R.id.tv_amount_buy);
                pricebuy = itemView.findViewById(R.id.tv_price_buy);
                numRight = itemView.findViewById(R.id.tv_num_right);
                amountsell = itemView.findViewById(R.id.tv_amount_sell);
                pricesell = itemView.findViewById(R.id.tv_price_sell);
            }
        }
    }
}
