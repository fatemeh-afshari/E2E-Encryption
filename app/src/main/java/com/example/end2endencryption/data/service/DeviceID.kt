package com.example.end2endencryption.data.service

import android.os.Build
import java.util.*

class DeviceID {
    fun getUniquePseudoID(): String {

        val devShortID =
            "35" + Build.BOARD.length % 10 + Build.BRAND.length % 10 + Build.CPU_ABI.length % 10 + Build.DEVICE.length % 10 + Build.MANUFACTURER.length % 10 + Build.MODEL.length % 10 + Build.PRODUCT.length % 10

        var serial: String?
        try {
            serial = Build::class.java.getField("SERIAL")[null]?.toString()
            return UUID(devShortID.hashCode().toLong(), serial.hashCode().toLong()).toString()
        } catch (exception: Exception) {
            serial = "serial"
        }

        return UUID(devShortID.hashCode().toLong(), serial.hashCode().toLong()).toString()
    }
}
