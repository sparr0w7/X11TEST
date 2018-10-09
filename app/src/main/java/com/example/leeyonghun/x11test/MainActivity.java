package com.example.leeyonghun.x11test;

import android.Manifest;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.leeyonghun.x11test.adapter.CoinAdapter;
import com.example.leeyonghun.x11test.data.Coin;
import com.example.leeyonghun.x11test.data.CoinType;
import com.example.leeyonghun.x11test.utils.BitcoinCashUtils;
import com.example.leeyonghun.x11test.utils.BitcoinUtils;
import com.example.leeyonghun.x11test.utils.DashUtils;
import com.example.leeyonghun.x11test.utils.LitecoinUtils;
import com.example.leeyonghun.x11test.utils.QtumUtils;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.bitcoinj.core.UTXO;
import org.bitcoinj.wallet.Wallet;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {
    final static String TAG = "MainActivity";
    ArrayList<UTXO> utxes;
    CoinAdapter adapter;
    ArrayList<Coin> coinList;
    Realm mRealm;
    ProgressDialog dialog;

    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dialog = new ProgressDialog(this);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.addItemDecoration(new
                DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));
        TedPermission.with(MainActivity.this)
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        realmInitailize(); //Ream 초기화

                        if(mRealm.isEmpty()) //지갑이 없을경우
                        {
                            new GeneratorWallet().execute(); // 지갑 생성
                        }else{
                            loadData();
                            coinListViewInitailize();
                        }
                    }

                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {
                        finish();
                    }
                })
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
    }


    //Realm 초기화
    public void realmInitailize()
    {
        mRealm.init(MainActivity.this);
        mRealm = Realm.getDefaultInstance();
    }

    //Realm 저장
    public void saveToWallet(final CoinType coinName, final String balance, final String address, final String path)
    {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Coin vo = realm.createObject(Coin.class);
                vo.setCoinName(coinName.name());
                vo.setBalance(balance);
                vo.setAddress(address);
                vo.setPath(path);
            }
        });
    }

    public void loadData()
    {
        RealmResults<Coin> result = mRealm.where(Coin.class).findAll();

        coinList = (ArrayList<Coin>) mRealm.copyFromRealm(result);
    }

    public void coinListViewInitailize()
    {
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        adapter = new CoinAdapter(coinList);
        recyclerView.setAdapter(adapter);
    }



    private class GeneratorWallet extends AsyncTask<Void, Void, Wallet[]> {

        @Override
        protected Wallet[] doInBackground(Void... data) {
            Wallet[] wallets = new Wallet[5];
            wallets[0] = BitcoinUtils.getInstance().createWallet(); //비트코인 지갑생성
            wallets[1] = BitcoinCashUtils.getInstance().createWallet(); // 비트코인캐시 지갑생성
            wallets[2] = QtumUtils.getInstance().createWallet(); // 퀀텀 지갑 생성
            wallets[3] = LitecoinUtils.getInstance().createWallet(); // 라이트코인 지갑 생성
            wallets[4] = DashUtils.getInstance().createWallet(); // 라이트코인 지갑 생성
            return wallets;
        }

        @Override
        protected void onPreExecute() {
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Wallet[] wallets) {
            dialog.dismiss();
            saveToWallet(CoinType.BITCOIN,"0 BTC",wallets[0].currentReceiveAddress().toString(),BitcoinUtils.getInstance().getPath());
            saveToWallet(CoinType.BITCOINCASH,"0 BCH",wallets[1].currentReceiveAddress().toString(),BitcoinCashUtils.getInstance().getPath());
            saveToWallet(CoinType.QTUM,"0 QTUM",wallets[2].currentReceiveAddress().toString(), QtumUtils.getInstance().getPath());
            saveToWallet(CoinType.LITECOIN,"0 LTC",wallets[3].currentReceiveAddress().toString(), LitecoinUtils.getInstance().getPath());
            saveToWallet(CoinType.DASH,"0 DASH",wallets[4].currentReceiveAddress().toString(), LitecoinUtils.getInstance().getPath());
            loadData();
            coinListViewInitailize();
        }
    }
}
