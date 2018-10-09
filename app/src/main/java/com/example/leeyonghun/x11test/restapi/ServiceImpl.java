package com.example.leeyonghun.x11test.restapi;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import retrofit2.Callback;

public interface ServiceImpl {
    public void getUnspentOutput(String address, Callback<JsonArray> callback);
    public void getBalance(String address, Callback<String> callback);
    public void sendRawTransaction(String data , Callback<JsonObject> callback);

}
