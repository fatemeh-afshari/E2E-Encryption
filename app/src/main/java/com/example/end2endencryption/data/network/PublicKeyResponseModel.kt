package com.example.end2endencryption.data.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PublicKeyResponseModel(
    @Json(name = "status") val status: String,
    @Json(name = "publicKey") val publicKey: String,
    @Json(name = "publicKeySpkiB64") val publicKeySpkiB64: String,
    @Json(name = "sharedKey") val sharedKey: String,
    @Json(name = "notHashedSecretKey") val notHashedSecretKey: String,
)
