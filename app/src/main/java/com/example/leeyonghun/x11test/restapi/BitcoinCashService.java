package com.example.leeyonghun.x11test.restapi;

import com.example.leeyonghun.x11test.AppApplication;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BitcoinCashService implements ServiceImpl{

    private final String BASE_URL= AppApplication.isMainnet ? "https://testnet.blockexplorer.com/" : "https://test-bch-insight.bitpay.com/";
    private static BitcoinCashService instance = new BitcoinCashService();

    private Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    private CoinAPI service = retrofit.create(CoinAPI.class);

    private BitcoinCashService() { }

    public static BitcoinCashService getInstance()
    {
        return instance;
    }


    ///해당 Address UnspentOutput 요청
    public void  getUnspentOutput(String address, Callback<JsonArray> callback)
    {
        Call<JsonArray> call = service.bchUnspentOutput(address);
        call.enqueue(callback);
    }

    ///해당 Address UnspentOutput 요청
    public void  getBalance(String address, Callback<String> callback)
    {
        Call<String> call = service.getBalanceBch(address);
        call.enqueue(callback);
    }

    @Override
    public void sendRawTransaction(String data, Callback<JsonObject> callback) {

    }
}
