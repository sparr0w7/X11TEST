package com.example.leeyonghun.x11test.restapi;

import com.example.leeyonghun.x11test.AppApplication;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class QtumService implements ServiceImpl{

    private final String BASE_URL= AppApplication.isMainnet ? "https://walletapi.qtum.org" : "https://testnet-walletapi.qtum.org";
    private static QtumService instance = new QtumService();

    private Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    private CoinAPI service = retrofit.create(CoinAPI.class);

    private QtumService() { }

    public static QtumService getInstance()
    {
        return instance;
    }


    ///해당 Address UnspentOutput 요청
    public void  getUnspentOutput(String address, Callback<JsonArray> callback)
    {
        Call<JsonArray> call = service.qtumUnspentOutput(address);
        call.enqueue(callback);
    }

    ///해당 Address UnspentOutput 요청
    public void  getBalance(String address, Callback<String> callback)
    {
       /* Call<String> call = service.getBalance(address);
        call.enqueue(callback);*/
    }

    @Override
    public void sendRawTransaction(String data, Callback<JsonObject> callback) {

    }
}
