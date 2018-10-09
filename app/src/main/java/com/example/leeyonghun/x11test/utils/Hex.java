package com.example.leeyonghun.x11test.utils;

import java.math.BigInteger;

public class Hex {

    public static byte[] decode(String data)
    {
        return new BigInteger(data,16).toByteArray();
    }
}
