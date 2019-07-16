package com.yjq.hotcoin;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;
import com.yjq.hotcoin.bean.Result;
import com.yjq.hotcoin.bean.Symbol;
import com.yjq.hotcoin.callback.JsonCallBack;

import java.util.ArrayList;
import java.util.List;

/**
 * 选择交易对
 */
public class SymbolActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    private List<Symbol> mSymbolList = new ArrayList<>();

    private RecyclerView mRv;
    private SymbolAdapter mAdapter;

    private SwipeRefreshLayout mSr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin);

        initView();

        getAllSymbols();
    }

    private void initView() {
        mRv = findViewById(R.id.rv_symbol);
        mRv.setLayoutManager(new GridLayoutManager(this,2));

        mAdapter = new SymbolAdapter(this,mSymbolList);
        mRv.setAdapter(mAdapter);

        mSr = findViewById(R.id.sr_symbol);
        mSr.setOnRefreshListener(this);
    }

    /**
     * 火币行情API(获取所有的交易对)
     */
    private void getAllSymbols(){
        OkGo.<String>get("https://api.huobi.pro/v1/common/symbols")
                .execute(new StringCallback() {

                    @Override
                    public void onStart(Request<String, ? extends Request> request) {
                        super.onStart(request);
                        mSr.setRefreshing(true);
                    }

                    @Override
                    public void onSuccess(Response<String> response) {
                        String result = response.body();
                        JsonObject object =  new JsonParser().parse(result).getAsJsonObject();
                        String status = object.get("status").getAsString();
                        if(status.equalsIgnoreCase("ok")){
                            JsonArray array = object.getAsJsonArray("data");
                            mSymbolList.clear();
                            for (int i=0;i<array.size();i++){
                                JsonObject ob = array.get(i).getAsJsonObject();
                                String part = ob.get("symbol-partition").getAsString();
                                if(part.equalsIgnoreCase("main")){
                                    Symbol symbol = new Symbol();
                                    symbol.base_currency = ob.get("base-currency").getAsString();
                                    symbol.quote_currency = ob.get("quote-currency").getAsString();
                                    symbol.price_precision = ob.get("price-precision").getAsString();
                                    symbol.amount_precision = ob.get("amount-precision").getAsString();
                                    symbol.symbol = symbol.base_currency + symbol.quote_currency;
                                    mSymbolList.add(symbol);
                                }
                            }
                            mAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        Toast.makeText(SymbolActivity.this,"网络发生错误或需要翻墙！",Toast.LENGTH_SHORT).show();
                        mSr.setRefreshing(false);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        mSr.setRefreshing(false);
                    }
                });

    }

    @Override
    public void onRefresh() {
        getAllSymbols();
    }

    class SymbolAdapter extends RecyclerView.Adapter<SymbolAdapter.SymbolHolder>{

        private List<Symbol> mList;
        private Context context;

        public SymbolAdapter(Context context,List<Symbol> symbolList){
            this.context = context;
            this.mList = symbolList;
        }

        @Override
        public SymbolHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_symbol,parent,false);
            SymbolHolder holder = new SymbolHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(SymbolHolder holder, final int position) {
            holder.base.setText(mList.get(position).base_currency.toUpperCase());
            holder.quote.setText(mList.get(position).quote_currency.toUpperCase());
            holder.card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context,KlineActivity.class);
                    intent.putExtra(KlineActivity.PARAM_SYMBOL,mList.get(position));
                    context.startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        class SymbolHolder extends RecyclerView.ViewHolder{
            public TextView base;
            public TextView quote;
            public CardView card;

            public SymbolHolder(View itemView) {
                super(itemView);
                card = itemView.findViewById(R.id.card);
                base = itemView.findViewById(R.id.tv_base);
                quote = itemView.findViewById(R.id.tv_quote);
            }
        }
    }
}
