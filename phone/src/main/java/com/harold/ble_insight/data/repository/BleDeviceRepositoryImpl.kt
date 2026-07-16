package com.harold.ble_insight.data.repository

import com.harold.ble_insight.data.ble.BleScanner
import com.harold.ble_insight.domain.model.BleDevice
import com.harold.ble_insight.domain.repository.BleDeviceRepository
import kotlinx.coroutines.flow.Flow

class BleDeviceRepositoryImpl(
    private val bleScanner: BleScanner
) : BleDeviceRepository {

    override fun scanDevices(scanPeriodMillis: Long): Flow<BleDevice> {
        return bleScanner.scan(scanPeriodMillis)
    }

    override fun stopScan() {
        bleScanner.stop()
    }
}
