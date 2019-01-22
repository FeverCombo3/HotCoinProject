package com.yjq.hotcoin.callback;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.lzy.okgo.callback.AbsCallback;
import com.yjq.hotcoin.bean.Result2;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;

/**
 * Created by yjq on 2018/5/21.
 */

public abstract class JsonCallBack2<T> extends AbsCallback<T> {


    @Override
    public T convertResponse(okhttp3.Response response) throws Throwable {
        Type genType = getClass().getGenericSuperclass();
        Type[] params = ((ParameterizedType)genType).getActualTypeArguments();

        Type type = params[0];
        if(!(type instanceof ParameterizedType)) throw new IllegalStateException("没有填写泛型");

        Type rawType = ((ParameterizedType)type).getRawType();

        Type typeArgument = ((ParameterizedType)type).getActualTypeArguments()[0];

        ResponseBody body = response.body();
        if(body == null) return null;
        Gson gson = new Gson();
        JsonReader jsonReader = new JsonReader(body.charStream());

        if(rawType != Result2.class){
            T data = gson.fromJson(jsonReader,type);
            response.close();
            return data;
        }else {
            Result2 result = gson.fromJson(jsonReader,type);
            response.close();

            String status = result.status;
            if(status.equals("ok")){
                return (T)result;
            }else {
                throw new IllegalStateException("出现错误");
            }
        }
    }
}
