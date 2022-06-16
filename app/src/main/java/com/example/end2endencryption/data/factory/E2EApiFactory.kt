package com.example.end2endencryption.data.factory

import com.example.end2endencryption.data.network.E2EApi
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

fun createE2EApi(): E2EApi = Retrofit
    .Builder()
    .baseUrl("http://10.21.10.94:9091/api/")
    .addConverterFactory(MoshiConverterFactory.create())
    .build()
    .create(E2EApi::class.java)
