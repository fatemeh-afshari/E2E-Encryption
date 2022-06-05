package com.example.end2endencryption.data.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PublicKeyResponseModel(
    @Json(name = "status") val status: String,
    @Json(name = "publicKey") val publicKey: String
)
