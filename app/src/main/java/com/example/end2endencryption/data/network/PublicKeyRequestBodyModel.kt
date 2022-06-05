package com.example.end2endencryption.data.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class PublicKeyRequestBodyModel(
    @Json(name = "publicKey") val publicKey: String
)