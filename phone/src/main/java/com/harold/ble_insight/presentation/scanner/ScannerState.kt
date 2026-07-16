package com.harold.ble_insight.presentation.scanner

import com.harold.ble_insight.domain.model.BleDevice

data class ScannerState(
    val isScanning: Boolean = false,
    val hasPermission: Boolean = false,
    val devices: List<BleDevice> = emptyList(),
    val message: String? = null
)
