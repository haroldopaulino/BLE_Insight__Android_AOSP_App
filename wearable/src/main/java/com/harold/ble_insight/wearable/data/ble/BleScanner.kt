package com.harold.ble_insight.wearable.data.ble

import com.harold.ble_insight.wearable.domain.model.BleDevice
import kotlinx.coroutines.flow.Flow

interface BleScanner {
    fun scan(scanPeriodMillis: Long): Flow<BleDevice>
    fun stop()
}
