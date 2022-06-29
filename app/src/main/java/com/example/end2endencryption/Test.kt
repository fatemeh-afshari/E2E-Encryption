package com.example.end2endencryption

import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo
import org.bouncycastle.jce.ECNamedCurveTable
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec
import org.bouncycastle.openssl.PEMParser
import org.bouncycastle.util.encoders.Base64
import java.io.StringReader
import java.security.*
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher
import javax.crypto.KeyAgreement
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec


var iv: ByteArray = SecureRandom().generateSeed(12)
const val TAG_LENGTH = 16
fun main() {
    Security.addProvider(BouncyCastleProvider())
    val plainText = "Hello World!!"
    println("Original plaintext message: $plainText")

    // Initialize two key pairs
    val keyPairA: KeyPair = generateECKeys()
    val keyPairB: KeyPair = generateECKeys()

    // Create two AES secret keys to encrypt/decrypt the message
    val secretKeyA: SecretKey = generateSharedSecret(keyPairA.private,
        keyPairB.public)
    val secretKeyB: SecretKey = generateSharedSecret(keyPairB.private,
        keyPairA.public)

    // Encrypt the message using 'secretKeyA'
    val cipherText: String = encrypt(secretKeyA, plainText)
    println("Encrypted cipher text: $cipherText")

    // Decrypt the message using 'secretKeyB'
    val decryptedPlainText: String = decrypt(secretKeyB, cipherText)
    println("Decrypted cipher text: $decryptedPlainText")
    val secretAStr = String(Base64.encode(secretKeyA.encoded))
    val secretBStr = String(Base64.encode(secretKeyB.encoded))
    println("secret key A: $secretAStr")
    println("secret key B: $secretBStr")
    val last65BytePubStr = String(Base64.encode(keyPairA.public.encoded.copyOfRange(keyPairA.public.encoded.size-66 , keyPairA.public.encoded.size-1)))
    val pubStr = String(Base64.encode(keyPairA.public.encoded))
    val prvStr = String(Base64.encode(keyPairA.private.encoded))

    val pemParser = PEMParser(StringReader("BMZI3HpgSBd6cHf+IfsQPw/NrpMioqNHOVJrKtci++rvWzYpdYwN+TftwTBhJFpPaigCNHekNuZrHKWPIMcL4YU="))
    val spki = pemParser.readObject() as SubjectPublicKeyInfo
    pemParser.close()
    val spkiEncoded = spki.encoded
    val kf = KeyFactory.getInstance("ECDH", "BC")

    val x509ks = X509EncodedKeySpec(
        Base64.decode(spkiEncoded))
    val pubKeyA = kf.generatePublic(x509ks)

    val p8ks = PKCS8EncodedKeySpec(
        Base64.decode(prvStr))
    val privKeyA = kf.generatePrivate(p8ks)



    println("-------------------TEST ENCODE & DECODE KEYS--------------------")
    println("public key A: ${keyPairA.public}")
    println("last 65 byte of public key A: $last65BytePubStr")
    println("encoded public key A: $pubStr")
    println("decoded public key A: $pubKeyA")

    println("private key A: ${keyPairA.private}")
    println("encoded private key A: $prvStr")
    println("decoded private key A: $privKeyA")
}

fun generateECKeys(): KeyPair {

    val parameterSpec: ECNamedCurveParameterSpec =
        ECNamedCurveTable.getParameterSpec("secp256k1")
    val keyPairGenerator: KeyPairGenerator = KeyPairGenerator.getInstance(
        "ECDH", "BC")
    keyPairGenerator.initialize(parameterSpec)
    return keyPairGenerator.generateKeyPair()

}

fun generateSharedSecret(
    privateKey: PrivateKey?,
    publicKey: PublicKey?,
): SecretKey {

    val keyAgreement: KeyAgreement = KeyAgreement.getInstance("ECDH", "BC")
    keyAgreement.init(privateKey)
    keyAgreement.doPhase(publicKey, true)
    return keyAgreement.generateSecret("AES")
}

fun encrypt(key: SecretKey?, plainText: String): String {
    val gcmSpec = GCMParameterSpec(TAG_LENGTH*8, iv)
    val cipher: Cipher = Cipher.getInstance("AES/GCM/NoPadding")
    val plainTextBytes = plainText.toByteArray(charset("UTF-8"))
    cipher.init(Cipher.ENCRYPT_MODE, key, gcmSpec)
    val cipherText = ByteArray(cipher.getOutputSize(plainTextBytes.size))
    var encryptLength: Int = cipher.update(plainTextBytes, 0,
        plainTextBytes.size, cipherText, 0)
    encryptLength += cipher.doFinal(cipherText, encryptLength)
    return String(Base64.encode(cipherText))
}

fun decrypt(key: SecretKey, cipherText: String): String {
    val decryptionKey: Key = SecretKeySpec(key.encoded,
        key.algorithm)
    val gcmSpec = GCMParameterSpec(TAG_LENGTH*8, iv)
    val cipher: Cipher = Cipher.getInstance("AES/GCM/NoPadding")
    val cipherTextBytes: ByteArray = Base64.decode(cipherText)
    cipher.init(Cipher.DECRYPT_MODE, decryptionKey, gcmSpec)
    val plainText = ByteArray(cipher.getOutputSize(cipherTextBytes.size))
    var decryptLength: Int = cipher.update(cipherTextBytes, 0,
        cipherTextBytes.size, plainText, 0)
    decryptLength += cipher.doFinal(plainText, decryptLength)
    return String(plainText, charset("UTF-8"))



}




