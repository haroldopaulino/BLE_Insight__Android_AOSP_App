package com.harold.ble_insight.domain.repository

import com.harold.ble_insight.domain.model.BleDevice
import kotlinx.coroutines.flow.Flow

interface BleDeviceRepository {
    fun scanDevices(scanPeriodMillis: Long): Flow<BleDevice>
    fun stopScan()
}
