package com.example.leeyonghun.x11test.utils;

import android.os.Environment;
import android.util.Log;

import com.example.leeyonghun.x11test.AppApplication;
import com.example.leeyonghun.x11test.data.CoinType;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.Context;
import org.bitcoinj.core.DumpedPrivateKey;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionConfidence;
import org.bitcoinj.core.TransactionOutPoint;
import org.bitcoinj.core.UTXO;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.params.QtumMainNetParams;
import org.bitcoinj.params.QtumTestNetParams;
import org.bitcoinj.script.Script;
import org.bitcoinj.wallet.DeterministicKeyChain;
import org.bitcoinj.wallet.UnreadableWalletException;
import org.bitcoinj.wallet.Wallet;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class QtumUtils implements UtilsImpl{

    private final String TAG = "QtumUtils";
    public Context params = new Context(AppApplication.isMainnet ? QtumMainNetParams.get() : QtumTestNetParams.get());
    private static QtumUtils instance = new QtumUtils();



    private QtumUtils(){}

    public static QtumUtils getInstance()
    {
        return instance;
    }

    public ArrayList<UTXO> jsonToUTXO(JsonArray jsonArray){
        ArrayList<UTXO> utxes = new ArrayList<UTXO>();

        for (JsonElement json : jsonArray) {

            JsonObject jsonData = json.getAsJsonObject();

            if(jsonData.get("confirmations").getAsInt() > 0 ) { // 노드로 부터 컨펌을 1이상 받은 값들만 가져옴
                utxes.add(new UTXO(new Sha256Hash(jsonData.get("tx_hash").getAsString()),
                        jsonData.get("vout").getAsInt(), //vout
                        Coin.parseCoin(jsonData.get("amount").getAsString()),
                        jsonData.get("block_height").getAsInt(),
                        true,
                        new Script(Hex.decode(jsonData.get("txout_scriptPubKey").getAsString()))));
            }
        }
        return utxes;
    }



    public Wallet createWallet()
    {
        Wallet wallet = new Wallet(params.getParams());
        ECKey ecKey = new ECKey();
        wallet.importKey(ecKey);

        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
        path = path+"/qtum.wallet";
        System.out.println(path);
        File filePath = new File(path);
        try{
            wallet.currentReceiveAddress();
            wallet.saveToFile(filePath);
        }catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }

        return wallet;
    }

    public boolean checkWallet(){
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
        path = path+"/qtum.wallet";
        File file = new File(path);

        return file.exists();
    }


    public String getPath()
    {
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
        path = path+"/qtum.wallet";
        return path;
    }


    public Wallet loadWallet()
    {
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
        path = path+"/qtum.wallet";

        Wallet wallet;
        System.out.println(path);

        try {
            wallet = Wallet.loadFromFile(new File(path));

        }catch (UnreadableWalletException e){
            e.printStackTrace();
            return null;
        }
        return wallet;
    }

    private String dumpPrivateKey(Wallet wallet ){
        DeterministicKeyChain kc = wallet.getActiveKeyChain();
        DeterministicKey rk = kc.getWatchingKey();
        Log.d(TAG, "dumpPrivateKey: " + rk.getPrivateKeyAsHex());
        Log.d(TAG, "dumpPrivateKey: " + rk.getPrivateKeyAsWiF(params.getParams()));
        Log.d(TAG, "dumpPrivateKey: " + rk.getPrivateKeyEncoded(params.getParams()).toBase58());
        return rk.getPrivateKeyAsWiF(params.getParams());
    }

    public String createTransaction(String toAddress , String amount, ArrayList<UTXO> utxos, Wallet wallet){
        Log.d(TAG, "currentReceiveKey: "+wallet.currentReceiveKey().getPrivateKeyAsWiF(params.getParams()));
        //String to a private key
        DumpedPrivateKey dumpedPrivateKey = DumpedPrivateKey.fromBase58(params.getParams(),
                dumpPrivateKey(wallet));

        /*ECKey key = dumpedPrivateKey.getKey();*/
        ECKey key = wallet.currentReceiveKey();

        //String to an address
        Address address2 = Address.fromString(params.getParams(), toAddress);


        Transaction tx = new Transaction(params.getParams());


        long fee = 100000; //수수료 0.001 QTUM
        //value is a sum of all inputs, fee is 4013
        tx.addOutput(Coin.valueOf(Coin.parseCoin(amount).value), address2);
        Log.d(TAG, "BTC: "+ Coin.parseCoin(amount).toString());
        Log.d(TAG, "satoshi: " + Coin.valueOf(Coin.parseCoin(amount).value));




        //utxos is an array of inputs from my wallet
        for(UTXO utxo : utxos)
        {
            Log.d(TAG, "send: " + utxo.toString());
            TransactionOutPoint outPoint = new TransactionOutPoint(params.getParams(), utxo.getIndex(), utxo.getHash());
            Log.d(TAG, "outpoint :" + outPoint.toString());
            Log.d(TAG, "script :" + utxo.getScript());
            Log.d(TAG, "return :  "+ Coin.valueOf(utxo.getValue().value - fee - Coin.parseCoin(amount).value).toString());
            tx.addOutput(Coin.valueOf(utxo.getValue().value - fee - Coin.parseCoin(amount).value),wallet.currentReceiveAddress());
            //YOU HAVE TO CHANGE THIS
            tx.addSignedInput(outPoint, utxo.getScript(), key, Transaction.SigHash.ALL, false);
            break;
        }




        tx.getConfidence().setSource(TransactionConfidence.Source.SELF);
        tx.setPurpose(Transaction.Purpose.USER_PAYMENT);
        String rawData = byteArrayToHex(tx.bitcoinSerialize());
        System.out.println(rawData);
        return rawData;

    }



    public static String byteArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder(a.length * 2);
        for(byte b: a)
            sb.append(String.format("%02x", b));
        return sb.toString();
    }
}
