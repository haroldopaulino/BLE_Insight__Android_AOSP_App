package com.harold.ble_insight.data.ble

import com.harold.ble_insight.domain.model.BleDevice
import kotlinx.coroutines.flow.Flow

interface BleScanner {
    fun scan(scanPeriodMillis: Long): Flow<BleDevice>
    fun stop()
}
