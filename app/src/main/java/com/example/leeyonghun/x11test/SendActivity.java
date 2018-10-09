package com.example.leeyonghun.x11test;

import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.leeyonghun.x11test.data.CoinType;
import com.example.leeyonghun.x11test.restapi.BitcoinCashService;
import com.example.leeyonghun.x11test.restapi.BitcoinService;
import com.example.leeyonghun.x11test.restapi.DashService;
import com.example.leeyonghun.x11test.restapi.LitecoinService;
import com.example.leeyonghun.x11test.restapi.QtumService;
import com.example.leeyonghun.x11test.restapi.ServiceImpl;
import com.example.leeyonghun.x11test.utils.BitcoinCashUtils;
import com.example.leeyonghun.x11test.utils.BitcoinUtils;
import com.example.leeyonghun.x11test.utils.DashUtils;
import com.example.leeyonghun.x11test.utils.LitecoinUtils;
import com.example.leeyonghun.x11test.utils.QtumUtils;
import com.example.leeyonghun.x11test.utils.UtilsImpl;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.bitcoinj.core.UTXO;
import org.bitcoinj.wallet.Wallet;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SendActivity extends AppCompatActivity {
    final String TAG = "SendActivity";
    EditText edit_to , edit_amount;
    ImageView coinImage;
    Context mContext;
    Button btn_send;
    ArrayList<UTXO> utxo ;
    Wallet wallet;

    ServiceImpl mService;
    UtilsImpl mUtils;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);

        mContext = this;

        Intent intent = getIntent();
        String coinName = intent.getStringExtra("coinName");
        String address = intent.getStringExtra("address");

        Log.d(TAG, "onCreate: "+ coinName);

        edit_to = findViewById(R.id.to);
        edit_amount = findViewById(R.id.amount);
        coinImage = findViewById(R.id.imageView);
        btn_send = findViewById(R.id.send);

        coinImage.setImageDrawable(getImage(coinName));
        mService = getService(coinName);
        mUtils = getUtils(coinName);
        wallet = mUtils.loadWallet();

        getUTXO(address);

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String rawData = mUtils.createTransaction(edit_to.getText().toString() , edit_amount.getText().toString(),utxo,wallet);
                sendRawTransacton(rawData);
            }
        });
    }

    private ServiceImpl getService(String coin)
    {
        switch (CoinType.valueOf(coin))
        {
            case BITCOIN:
                return BitcoinService.getInstance();
            case BITCOINCASH:
                return BitcoinCashService.getInstance();
            case QTUM:
                return QtumService.getInstance();
            case LITECOIN:
                return LitecoinService.getInstance();
            default:
                return QtumService.getInstance();
        }
    }

    private UtilsImpl getUtils(String coin)
    {
        switch (CoinType.valueOf(coin))
        {
            case BITCOIN:
                return BitcoinUtils.getInstance();
            case BITCOINCASH:
                return BitcoinCashUtils.getInstance();
            case QTUM:
                return QtumUtils.getInstance();
            case LITECOIN:
                return LitecoinUtils.getInstance();
            case DASH:
                return DashUtils.getInstance();
            default:
                return QtumUtils.getInstance();
        }
    }

    private Drawable getImage(String coin)
    {
        switch (CoinType.valueOf(coin))
        {
            case BITCOIN:
                return mContext.getDrawable(R.drawable.btc_logo);
            case BITCOINCASH:
                return mContext.getDrawable(R.drawable.bch_logo);
            case QTUM:
                return mContext.getDrawable(R.drawable.qtum_logo);
            case LITECOIN:
                return mContext.getDrawable(R.drawable.ltc_logo);
            case DASH:
                return mContext.getDrawable(R.drawable.dash_logo);
            default:
                return mContext.getDrawable(R.drawable.qtum_logo);
        }
    }



    public void getUTXO(String address){
        mService.getUnspentOutput(address, new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {

                if(response.isSuccessful()) {
                    utxo  = mUtils.jsonToUTXO(response.body());
                }else{
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
            }
        });
    }

    public void sendRawTransacton(String data){
        mService.sendRawTransaction(data, new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                        if(response.isSuccessful()) {
                            Toast.makeText(mContext,"전송 성공",Toast.LENGTH_SHORT).show();
                            ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData clip = null;
                                clip = ClipData.newPlainText("", response.body().get("txid").getAsString());

                    clipboard.setPrimaryClip(clip);
                }else{
                    Toast.makeText(mContext,"전송 실패",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
            }
        });
    }

}
