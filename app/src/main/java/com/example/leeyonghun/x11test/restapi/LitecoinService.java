package com.example.leeyonghun.x11test.restapi;

import com.example.leeyonghun.x11test.AppApplication;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LitecoinService implements ServiceImpl{

    private final String BASE_URL= AppApplication.isMainnet ? "https://walletapi.qtum.org" : "http://explorer.litecointools.com";
    private static LitecoinService instance = new LitecoinService();

    private Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    private CoinAPI service = retrofit.create(CoinAPI.class);

    private LitecoinService() { }

    public static LitecoinService getInstance()
    {
        return instance;
    }


    ///해당 Address UnspentOutput 요청
    public void  getUnspentOutput(String address, Callback<JsonArray> callback)
    {
        Call<JsonArray> call = service.liteUnspentOutput(address);
        call.enqueue(callback);
    }

    ///해당 Address UnspentOutput 요청
    public void  getBalance(String address, Callback<String> callback)
    {
        Call<String> call = service.getBalanceLite(address);
        call.enqueue(callback);
    }

    public void sendRawTransaction(String data , Callback<JsonObject> callback)
    {
        JsonObject json = new JsonObject();
        json.addProperty("rawtx",data);
        Call<JsonObject> call = service.sendRawTransaction(json);
        call.enqueue(callback);
    }
}
