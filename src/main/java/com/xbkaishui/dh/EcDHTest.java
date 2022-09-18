package com.xbkaishui.dh;

import com.google.crypto.tink.subtle.Hex;
import com.google.crypto.tink.subtle.X25519;

import java.security.GeneralSecurityException;


public class EcDHTest {

    public static void main(String[] args) throws GeneralSecurityException {
        byte[] secretKey = X25519.generatePrivateKey();
        byte[] shared = X25519.computeSharedSecret(secretKey, Hex.decode("422c8e7a6227d7bca1350b3e2bb7279f7897b87bb6854b783c60e80311ae3079"));
        System.out.println(Hex.encode(shared));
        int count = 1000000;
        long start = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            X25519.computeSharedSecret(secretKey, Hex.decode("422c8e7a6227d7bca1350b3e2bb7279f7897b87bb6854b783c60e80311ae3079"));
        }
        long end = System.currentTimeMillis();
        System.out.println("cost " + (end - start) + " ms");
    }
}
