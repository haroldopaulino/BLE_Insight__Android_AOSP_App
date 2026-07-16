package com.harold.ble_insight.wearable.presentation

import com.harold.ble_insight.wearable.domain.model.BleDevice

data class ScannerState(
    val isScanning: Boolean = false,
    val hasPermission: Boolean = false,
    val devices: List<BleDevice> = emptyList(),
    val message: String? = null
)
