package com.yjq.hotcoin;

import android.app.Application;
import android.content.Context;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.https.HttpsUtils;
import com.lzy.okgo.interceptor.HttpLoggingInterceptor;

import java.io.IOException;
import java.util.logging.Level;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by yjq
 * 2018/5/21.
 */

public class MyApp extends Application{

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        //方法一：信任所有证书,不安全有风险
//        HttpsUtils.SSLParams sslParams1 = HttpsUtils.getSslSocketFactory();
//        builder.sslSocketFactory(sslParams1.sSLSocketFactory, sslParams1.trustManager);
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor("OkGo");

        loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY);

        loggingInterceptor.setColorLevel(Level.INFO);

        builder.addInterceptor(loggingInterceptor);


        builder.addNetworkInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                Request.Builder builder = original.newBuilder();

                builder.removeHeader("User-Agent").addHeader("User-Agent", getUserAgent()).build();

                builder.method(original.method(), original.body());

                Request request = builder.build();

                return chain.proceed(request);
            }
        });

        OkGo.getInstance().init(this)
                .setOkHttpClient(builder.build());

        context = getApplicationContext();
    }


    private String getUserAgent(){
        return "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.71 Safari/537.36";
    }

    public static Context getContext(){
        return context;
    }
}
