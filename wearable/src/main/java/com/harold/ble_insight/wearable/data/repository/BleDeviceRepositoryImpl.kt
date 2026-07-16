package com.harold.ble_insight.wearable.data.repository

import com.harold.ble_insight.wearable.data.ble.BleScanner
import com.harold.ble_insight.wearable.domain.model.BleDevice
import com.harold.ble_insight.wearable.domain.repository.BleDeviceRepository
import kotlinx.coroutines.flow.Flow

class BleDeviceRepositoryImpl(
    private val scanner: BleScanner
) : BleDeviceRepository {
    override fun scanDevices(scanPeriodMillis: Long): Flow<BleDevice> = scanner.scan(scanPeriodMillis)

    override fun stopScan() = scanner.stop()
}
