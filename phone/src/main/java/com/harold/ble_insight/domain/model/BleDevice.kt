package com.harold.ble_insight.domain.model

data class BleDevice(
    val name: String,
    val address: String,
    val rssi: Int,
    val type: String,
    val bondState: String,
    val advertiseFlags: Int?,
    val addressType: String,
    val advertisingType: String,
    val primaryPhy: String,
    val secondaryPhy: String,
    val txPower: Int?,
    val isConnectable: Boolean,
    val isLegacy: Boolean,
    val serviceUuids: List<String>,
    val manufacturerData: List<String>,
    val serviceData: List<String>,
    val lastSeenMillis: Long
)
