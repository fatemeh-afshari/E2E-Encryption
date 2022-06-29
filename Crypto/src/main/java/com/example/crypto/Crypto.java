package com.example.crypto;


import java.util.Base64;
import java.security.KeyPair;
import java.security.KeyPairGenerator;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

public class Crypto {
    private KeyPair serverKeyPair;
    private SecretKey serverKey;
    private PublicKey mibPublicKey;
    private KeyPairGenerator kpg;

    // test
    private KeyPair mibKeyPair;
    private SecretKey mibKey;


    public static void main(String[] args) {
        Crypto testCrypto = new Crypto();

        try {
            testCrypto.generateServerKeyPair();
//             SecretKey sharedKey = testCrypto.setMibPublicKey("MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAER5ptsNnY5LozRM9TeDH2KamDNHHGMgTfjN+NchU/KQzEch+/pU7Lhq5BvPlKiexQ/4PBlYQvq2xcYgKqFI+R4A==");
            SecretKey sharedKey = testCrypto.setMibPublicKey("MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAE6zkVzp6x1/kY3ifTj4uiPXWSHcrubJAhsWJLhW31lSaq1jz0MuCR8rcR1Zh550zGT5k/B4G69QLtnj5LMdkHiQ==");

            String key = "dusneD0s+7hPg57yJGBfuGrj3m8sCg9g4VbBZOuTTHQ=";
            byte[] keyDecoded = Base64.getDecoder().decode(key);
            System.out.println("fucked up: " + keyDecoded);
            SecretKeySpec keySpec = new SecretKeySpec(keyDecoded, "SHA256");
            String sharedKey2 = new String(Base64.getEncoder().encode(keySpec.getEncoded()));

            System.out.println("SharedKey2: " + sharedKey2);

//             System.out.println("Server PublicKey");
//             System.out.println(testCrypto.getPublicKey());
            System.out.println("Encrypted");
            String encrypted = Crypto.encrypt(keySpec, "Aa123456");
            System.out.println(encrypted);
            System.out.println("Decrypt");
            System.out.println(Crypto.decrypt(keySpec, encrypted));

            System.out.println("Decrypt Client Message");
            System.out.println("Decrypted Client Message: " + Crypto.decrypt(keySpec, "D8wkfnUuilwuOSD4RKtc9n4bEY7yX9k671UFzbv0+LeWkf8/"));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void generateServerKeyPair() {
        serverKeyPair = genDHKeyPair();
    }

    public String getPublicKey() {
        String result = "";
        try {
            KeyFactory fact = KeyFactory.getInstance("EC");
            X509EncodedKeySpec spec = fact.getKeySpec(serverKeyPair.getPublic(), X509EncodedKeySpec.class); // change from PublicKey to ASN1
            result = new String(Base64.getEncoder().encode(spec.getEncoded()));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            System.out.println(e.getMessage());
        }
        return result;
    }

    public SecretKey setMibPublicKey(String publicKey) throws Exception, NoSuchAlgorithmException, InvalidKeySpecException {   // MINE
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKey));  // Change ASN1 to publicKey
        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        mibPublicKey = keyFactory.generatePublic(keySpec);
        serverKey = Crypto.agreeSecretKey(serverKeyPair.getPrivate(), mibPublicKey, true);
        String sharedKey = new String(Base64.getEncoder().encode(serverKey.getEncoded()));
        System.out.println("SharedKey");
        System.out.println(sharedKey);

        return serverKey;
    }

    public static SecretKey agreeSecretKey(PrivateKey prk_self, PublicKey pbk_peer, boolean lastPhase) throws Exception {  // THEIRS
        SecretKey desSpec = null;
        try {
            KeyAgreement keyAgree = KeyAgreement.getInstance("ECDH");
            keyAgree.init(prk_self);
            keyAgree.doPhase(pbk_peer, true);
            byte[] sec = keyAgree.generateSecret();
            desSpec = new SecretKeySpec(sec, "AES");
        } catch (NoSuchAlgorithmException e) {
            System.out.println(e.getMessage());
            throw new Exception();
        } catch (InvalidKeyException e) {
            System.out.println(e.getMessage());
            throw new Exception();
        } catch (IllegalStateException iexp) {
            System.out.println(iexp.getMessage());
            throw new Exception();
        }
        return desSpec;
    }

    public static String encrypt(SecretKeySpec key, String src) {
        try {
            byte[] srcByte = src.getBytes();
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] iv = cipher.getIV(); // See question #1
            assert iv.length == 12; // See question #2
            byte[] cipherText = cipher.doFinal(srcByte);
            if (cipherText.length != srcByte.length + 16)
                throw new IllegalStateException();// See question #3
            byte[] message = new byte[12 + srcByte.length + 16]; // See question #4
            System.arraycopy(iv, 0, message, 0, 12);
            System.arraycopy(cipherText, 0, message, 12, cipherText.length);
            return new String(Base64.getEncoder().encode(message));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            System.out.println(e.getMessage());
        }
        return "";
    }

    public static String decrypt(SecretKeySpec key, String messageStr) {
        try {
            byte[] message = Base64.getDecoder().decode(messageStr.getBytes());
            if (message.length < 12 + 16) throw new IllegalArgumentException();
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec params = new GCMParameterSpec(128, message, 0, 12);
            String testSecret = new String(Base64.getEncoder().encode(key.getEncoded()));
            System.out.println("testSecret");
            System.out.println(testSecret);
            cipher.init(Cipher.DECRYPT_MODE, key, params);
            byte[] plaintext = cipher.doFinal(message, 12, message.length - 12);

            return new String(plaintext, "utf-8");
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException |
                NoSuchPaddingException |
                InvalidKeyException |
                BadPaddingException |
                IllegalBlockSizeException |
                InvalidAlgorithmParameterException e) {
            System.out.println(e.getMessage());
        }
        return "";
    }

    public KeyPair genDHKeyPair() {
        try {
            kpg = KeyPairGenerator.getInstance("EC");
            ECGenParameterSpec ecsp = new ECGenParameterSpec("secp256k1");
            kpg.initialize(ecsp);
        } catch (NoSuchAlgorithmException e) {
            System.out.println(e.getMessage());
        } catch (InvalidAlgorithmParameterException e) {
            System.out.println(e.getMessage());
        }

        return kpg.genKeyPair();
    }

}