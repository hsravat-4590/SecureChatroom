package com.ravat.hanzalah.securechat.se.crypto;

import javax.crypto.*;
import java.io.IOException;
import java.io.Serializable;
import java.security.*;

/**
 * Singleton instance holds an RSA Public and Private Key Pair
 */
public class RSA {
    private final PrivateKey privateKey;
    public final PublicKey publicKey;
    private static RSA instance;

    public static RSA getInstance(){
        if(instance == null){
            try {
                instance = new RSA();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }
    private RSA() throws NoSuchAlgorithmException{
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        KeyPair keyPair = generator.generateKeyPair();
        privateKey = keyPair.getPrivate();
        publicKey = keyPair.getPublic();
    }
    /**
     * Encrypt a Message using another user's public key
     * @param message The message to encrypt
     * @return a SealedObject which holds the encrypted message.
     */
    public SealedObject encrypt(Serializable message, PublicKey publicKey){
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return new SealedObject(message,cipher);
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Decrypts a SealedObject using the private key
     * @param message The Sealed Object
     * @return
     */
    public Object decrypt(SealedObject message){
        try{
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE,privateKey);
            Object secretMessage = message.getObject(cipher);
            return message.getObject(cipher);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
