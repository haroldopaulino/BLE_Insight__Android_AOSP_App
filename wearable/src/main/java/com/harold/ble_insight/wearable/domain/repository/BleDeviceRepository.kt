package com.harold.ble_insight.wearable.domain.repository

import com.harold.ble_insight.wearable.domain.model.BleDevice
import kotlinx.coroutines.flow.Flow

interface BleDeviceRepository {
    fun scanDevices(scanPeriodMillis: Long): Flow<BleDevice>
    fun stopScan()
}
