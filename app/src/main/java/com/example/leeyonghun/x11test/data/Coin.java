package com.example.leeyonghun.x11test.data;

import android.media.Image;

import org.bitcoinj.core.UTXO;
import org.bitcoinj.wallet.Wallet;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmObject;


public class Coin extends RealmObject {


    private String coinName;
    private String balance;
    private String address;
    private String path;

    public String getCoinName() {
        return coinName;
    }

    public void setCoinName(String coinName) {
        this.coinName = coinName;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Coin(String coinName, String balance, String address, String path) {
        this.coinName = coinName;
        this.balance = balance;
        this.address = address;
        this.path = path;
    }

    public Coin(){}






}
