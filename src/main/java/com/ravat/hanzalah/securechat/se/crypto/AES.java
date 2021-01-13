package com.ravat.hanzalah.securechat.se.crypto;

import javax.crypto.*;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Class to facilitate AES encryption
 */
public class AES {
    private final SecretKey key;
    private final Cipher mEncryptCipher;
    private final Cipher mDecryptCipher;
    /**
     * Construct an AES Encryption Facility using a predefined key
     * @param key
     */
    public AES(SecretKey key){
        this.key = key;
        Cipher eCipher = null;
        Cipher dCipher = null;
        try{
            eCipher = Cipher.getInstance("AES");
            dCipher = Cipher.getInstance("AES");
            eCipher.init(Cipher.ENCRYPT_MODE,key);
            dCipher.init(Cipher.DECRYPT_MODE,key);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException exception){}
        mEncryptCipher = eCipher;
        mDecryptCipher = dCipher;

    }

    /**
     * Construct an AES Encryption Facility and generate a key
     */
    public AES(){
        KeyGenerator keyGenerator = null;
        try {
            keyGenerator = KeyGenerator.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        keyGenerator.init(256);
        key = keyGenerator.generateKey();
        Cipher eCipher = null;
        Cipher dCipher = null;
        try{
            eCipher = Cipher.getInstance("AES");
            dCipher = Cipher.getInstance("AES");
            eCipher.init(Cipher.ENCRYPT_MODE,key);
            dCipher.init(Cipher.DECRYPT_MODE,key);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException exception){}
        mEncryptCipher = eCipher;
        mDecryptCipher = dCipher;
    }

    /**
     * Encrypts a Message
     * @param message
     * @return
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public String encrypt(String message) throws BadPaddingException, IllegalBlockSizeException {
        return Base64.getEncoder().encodeToString(mEncryptCipher.doFinal(message.getBytes()));
    }

    /**
     * Decrypts a Message
     * @param cipherText
     * @return
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public String decrypt(String cipherText) throws BadPaddingException, IllegalBlockSizeException {
        return new String(mDecryptCipher.doFinal(Base64.getDecoder().decode(cipherText)));
    }

    public SecretKey getKey(){return key;}
}
