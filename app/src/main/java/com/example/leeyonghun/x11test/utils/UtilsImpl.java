package com.example.leeyonghun.x11test.utils;

import com.google.gson.JsonArray;

import org.bitcoinj.core.Coin;
import org.bitcoinj.core.UTXO;
import org.bitcoinj.wallet.Wallet;

import java.util.ArrayList;

public interface UtilsImpl {
    public ArrayList<UTXO> jsonToUTXO(JsonArray jsonArray);

    public Wallet createWallet();

    public Wallet loadWallet();

    public String createTransaction(String toAddress , String amount, ArrayList<UTXO> utxos, Wallet wallet);

}
