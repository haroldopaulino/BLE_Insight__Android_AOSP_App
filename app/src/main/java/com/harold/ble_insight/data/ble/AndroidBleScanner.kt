package com.harold.ble_insight.data.ble

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.os.Build
import com.harold.ble_insight.domain.model.BleDevice
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

class AndroidBleScanner(
    private val context: Context
) : BleScanner {

    private val bluetoothManager by lazy {
        context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    }

    private val bluetoothAdapter
        get() = bluetoothManager.adapter

    private val scanner
        get() = bluetoothAdapter?.bluetoothLeScanner

    private var activeCallback: ScanCallback? = null

    @SuppressLint("MissingPermission")
    override fun scan(scanPeriodMillis: Long): Flow<BleDevice> = callbackFlow {
        stop()

        val bluetoothLeScanner = scanner
        if (bluetoothLeScanner == null) {
            close(IllegalStateException("Bluetooth LE scanner is not available."))
            return@callbackFlow
        }

        val settings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
            .setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE)
            .setNumOfMatches(ScanSettings.MATCH_NUM_MAX_ADVERTISEMENT)
            .setReportDelay(0L)
            .build()

        val callback = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult) {
                trySend(result.toBleDevice())
            }

            override fun onBatchScanResults(results: MutableList<ScanResult>) {
                results.forEach { trySend(it.toBleDevice()) }
            }

            override fun onScanFailed(errorCode: Int) {
                close(IllegalStateException(errorCode.toScanErrorMessage()))
            }
        }

        activeCallback = callback

        val startResult = runCatching {
            bluetoothLeScanner.startScan(null, settings, callback)
        }

        startResult.onFailure {
            activeCallback = null
            close(it)
        }

        if (startResult.isSuccess) {
            launch {
                delay(scanPeriodMillis)
                stop()
                close()
            }
        }

        awaitClose { stop() }
    }

    @SuppressLint("MissingPermission")
    override fun stop() {
        activeCallback?.let { callback ->
            runCatching { scanner?.stopScan(callback) }
        }
        activeCallback = null
    }

    @SuppressLint("MissingPermission")
    private fun ScanResult.toBleDevice(): BleDevice {
        val record = scanRecord
        return BleDevice(
            name = runCatching { device.name?.takeIf(String::isNotBlank) }.getOrNull() ?: "Unknown Device",
            address = runCatching { device.address }.getOrNull() ?: "Unknown",
            rssi = rssi,
            type = device.type.toDeviceType(),
            bondState = device.bondState.toBondState(),
            advertiseFlags = record?.advertiseFlags?.takeIf { it >= 0 },
            addressType = device.addressTypeLabel(),
            advertisingType = if (isConnectable) "Connectable" else "Non-connectable",
            primaryPhy = primaryPhy.toPhyLabel(),
            secondaryPhy = secondaryPhy.toPhyLabel(),
            txPower = txPower.takeUnless { it == ScanResult.TX_POWER_NOT_PRESENT },
            isConnectable = isConnectable,
            isLegacy = isLegacy,
            serviceUuids = record?.serviceUuids?.map { it.uuid.toString() }.orEmpty(),
            manufacturerData = record?.manufacturerSpecificData.toDisplayValues(),
            serviceData = record?.serviceData?.map { (uuid, data) ->
                "${uuid.uuid}: ${data.toHex()}"
            }.orEmpty(),
            lastSeenMillis = System.currentTimeMillis()
        )
    }

    @SuppressLint("MissingPermission")
    private fun BluetoothDevice.addressTypeLabel(): String {
        return if (Build.VERSION.SDK_INT >= 35) {
            when (addressType) {
                BluetoothDevice.ADDRESS_TYPE_PUBLIC -> "Public"
                BluetoothDevice.ADDRESS_TYPE_RANDOM -> "Random"
                BluetoothDevice.ADDRESS_TYPE_ANONYMOUS -> "Anonymous"
                else -> "Unknown"
            }
        } else {
            "Unknown"
        }
    }

    private fun android.util.SparseArray<ByteArray>?.toDisplayValues(): List<String> {
        if (this == null) return emptyList()
        return buildList {
            for (index in 0 until size()) {
                add("0x${keyAt(index).toString(16).uppercase()}: ${valueAt(index).toHex()}")
            }
        }
    }

    private fun ByteArray.toHex(): String {
        return joinToString(" ") { "%02X".format(it) }
    }

    private fun Int.toDeviceType(): String {
        return when (this) {
            BluetoothDevice.DEVICE_TYPE_CLASSIC -> "Classic"
            BluetoothDevice.DEVICE_TYPE_LE -> "BLE"
            BluetoothDevice.DEVICE_TYPE_DUAL -> "Dual"
            else -> "Unknown"
        }
    }

    private fun Int.toBondState(): String {
        return when (this) {
            BluetoothDevice.BOND_BONDED -> "Bonded"
            BluetoothDevice.BOND_BONDING -> "Bonding"
            BluetoothDevice.BOND_NONE -> "Not bonded"
            else -> "Unknown"
        }
    }

    private fun Int.toPhyLabel(): String {
        return when (this) {
            BluetoothDevice.PHY_LE_1M -> "1M"
            BluetoothDevice.PHY_LE_2M -> "2M"
            BluetoothDevice.PHY_LE_CODED -> "Coded"
            0 -> "N/A"
            else -> "Unknown"
        }
    }

    private fun Int.toScanErrorMessage(): String {
        return when (this) {
            ScanCallback.SCAN_FAILED_ALREADY_STARTED -> "BLE scan is already running."
            ScanCallback.SCAN_FAILED_APPLICATION_REGISTRATION_FAILED -> "BLE scan registration failed. Toggle Bluetooth off and on, then try again."
            ScanCallback.SCAN_FAILED_FEATURE_UNSUPPORTED -> "BLE scanning is not supported on this device."
            ScanCallback.SCAN_FAILED_INTERNAL_ERROR -> "Android reported an internal Bluetooth scan error."
            ScanCallback.SCAN_FAILED_OUT_OF_HARDWARE_RESOURCES -> "Bluetooth scan resources are unavailable."
            ScanCallback.SCAN_FAILED_SCANNING_TOO_FREQUENTLY -> "Android blocked frequent scans. Wait a few seconds and try again."
            else -> "BLE scan failed with error code $this."
        }
    }
}
