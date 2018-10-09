package com.example.leeyonghun.x11test.restapi;

import com.example.leeyonghun.x11test.AppApplication;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface CoinAPI {

    /*BitCoin*/
    @GET("/api/addrs/{address}/utxo")
    Call<JsonArray> btcUnspentOutput(@Path("address") String address);

    @GET("/api/addr/{address}/balance")
    Call<String> getBalanceBtc(@Path("address") String address);



    /*BitcoinCash*/
    @GET("/api/addrs/{address}/utxo")
    Call<JsonArray> bchUnspentOutput(@Path("address") String address);

    @GET("/api/addr/{address}/balance")
    Call<String> getBalanceBch(@Path("address") String address);


    /*Qtum*/
    @GET("/outputs/unspent/{address}")
    Call<JsonArray> qtumUnspentOutput(@Path("address") String address);

    /*Litecoin*/
    @GET("/api/addrs/{address}/utxo")
    Call<JsonArray> liteUnspentOutput(@Path("address") String address);

    @GET("/api/addr/{address}/balance")
    Call<String> getBalanceLite(@Path("address") String address);

    @Headers("Content-Type: application/json")
    @POST("/api/tx/send")
    Call<JsonObject> sendRawTransaction(@Body JsonObject body);
}
