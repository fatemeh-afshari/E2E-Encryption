package com.example.end2endencryption.data.mapper

import com.example.end2endencryption.data.model.DecryptedDataModel
import com.example.end2endencryption.data.model.PublicKeyModel
import com.example.end2endencryption.data.network.CheckSecretKeyResponseModel
import com.example.end2endencryption.data.network.PublicKeyResponseModel

fun PublicKeyResponseModel.toPublicKey(): PublicKeyModel = PublicKeyModel(status, publicKey)

fun CheckSecretKeyResponseModel.toEncryptedData(): DecryptedDataModel = DecryptedDataModel(status, data)
