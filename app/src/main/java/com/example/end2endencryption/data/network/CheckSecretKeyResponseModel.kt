package com.example.end2endencryption.data.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CheckSecretKeyResponseModel(
    @Json(name = "status") val status: String,
    @Json(name = "data") val data: String
)
