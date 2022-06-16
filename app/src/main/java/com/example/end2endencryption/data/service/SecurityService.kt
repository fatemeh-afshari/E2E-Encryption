package com.example.end2endencryption.data.service

import android.util.Log
import org.bouncycastle.jce.ECNamedCurveTable
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec
import org.bouncycastle.util.encoders.Base64
import java.security.*
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import javax.crypto.*
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec


class SecurityService {
    private var iv: ByteArray= SecureRandom().generateSeed(12)// Nonce
    private val tagLength = 16

    private var secretKey: SecretKey? = null


    fun generateECKeys(): KeyPair {
        Security.addProvider(BouncyCastleProvider())
        val parameterSpec: ECNamedCurveParameterSpec =
            ECNamedCurveTable.getParameterSpec("prime256v1")
        val keyPairGenerator: KeyPairGenerator = KeyPairGenerator.getInstance(
            "ECDH", BouncyCastleProvider())
        keyPairGenerator.initialize(parameterSpec)
        val pair =  keyPairGenerator.generateKeyPair()
        Log.v("PublicKey", String(Base64.encode(pair.public.encoded)))
        return  pair
    }

    fun encodeKey(key: ByteArray): String {
        return String(Base64.encode(key))

    }

    fun decodePublicKey(strKey: String): PublicKey {
        val x509ks = X509EncodedKeySpec(
            Base64.decode(strKey))
       val kf = KeyFactory.getInstance("ECDH", BouncyCastleProvider())
        return kf.generatePublic(x509ks)

    }

    fun decodePrivateKey(strKey: String): PrivateKey {
        val p8ks = PKCS8EncodedKeySpec(
            Base64.decode(strKey))
        val kf = KeyFactory.getInstance("ECDH", BouncyCastleProvider())
        return kf.generatePrivate(p8ks)

    }

    fun generateSharedSecret(
        privateKey: PrivateKey?,
        publicKey: PublicKey?,
    ): SecretKey {

        val keyAgreement: KeyAgreement = KeyAgreement.getInstance("ECDH" , BouncyCastleProvider())
        keyAgreement.init(privateKey)
        keyAgreement.doPhase(publicKey, true)
        val sharedSecret = keyAgreement.generateSecret("AES")
        secretKey = sharedSecret
        return sharedSecret
    }

    fun encrypt(plainText: String): String {
        secretKey?.let {
        val gcmSpec = GCMParameterSpec(tagLength * 8, com.example.end2endencryption.iv)
        val cipher: Cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val plainTextBytes = plainText.toByteArray(charset("UTF-8"))
        cipher.init(Cipher.ENCRYPT_MODE, it, gcmSpec)
        val cipherText = ByteArray(cipher.getOutputSize(plainTextBytes.size))
        var encryptLength: Int = cipher.update(plainTextBytes, 0,
            plainTextBytes.size, cipherText, 0)
        encryptLength += cipher.doFinal(cipherText, encryptLength)
        return String(Base64.encode(cipherText))
        } ?: return "Null Secret Key"
    }

    fun decrypt(cipherText: String): String {
        secretKey?.let {
            val decryptionKey: Key = SecretKeySpec(it.encoded,
                it.algorithm)
            val gcmSpec = GCMParameterSpec(tagLength * 8, com.example.end2endencryption.iv)
            val cipher: Cipher = Cipher.getInstance("AES/GCM/NoPadding")
            val cipherTextBytes: ByteArray = Base64.decode(cipherText)
            cipher.init(Cipher.DECRYPT_MODE, decryptionKey, gcmSpec)
            val plainText = ByteArray(cipher.getOutputSize(cipherTextBytes.size))
            var decryptLength: Int = cipher.update(cipherTextBytes, 0,
                cipherTextBytes.size, plainText, 0)
            decryptLength += cipher.doFinal(plainText, decryptLength)
            return String(plainText, charset("UTF-8"))
        } ?: return "Null Secret Key"

    }
}