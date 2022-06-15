package com.example.end2endencryption.data.model


data class PublicKeyModel(
    val status: String,
    val publicKey: String,
    val publicKeySpkiB64: String,
    val sharedKey: String,
    val notHashedSecretKey: String,


    )
