package com.harold.ble_insight.wearable.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.harold.ble_insight.wearable.domain.model.BleDevice
import com.harold.ble_insight.wearable.domain.repository.BleDeviceRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ScannerViewModel(
    private val repository: BleDeviceRepository
) : ViewModel() {

    var state by mutableStateOf(ScannerState())
        private set

    private var scanJob: Job? = null
    private val scanPeriodMillis = 30_000L

    fun onPermissionResult(isGranted: Boolean) {
        state = state.copy(
            hasPermission = isGranted,
            message = if (isGranted) null else "Nearby devices permission required."
        )
    }

    fun startScan() {
        if (state.isScanning) return
        state = state.copy(isScanning = true, devices = emptyList(), message = null)
        scanJob?.cancel()
        scanJob = viewModelScope.launch {
            try {
                repository.scanDevices(scanPeriodMillis).collect { device ->
                    state = state.copy(devices = state.devices.upsert(device))
                }
                state = state.copy(
                    isScanning = false,
                    message = if (state.devices.isEmpty()) "No devices found." else null
                )
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: SecurityException) {
                repository.stopScan()
                state = state.copy(isScanning = false, message = "Bluetooth permission denied.")
            } catch (exception: Throwable) {
                repository.stopScan()
                state = state.copy(isScanning = false, message = exception.message ?: "Scan failed.")
            }
        }
    }

    fun onBluetoothUnavailable() {
        state = state.copy(isScanning = false, message = "Bluetooth unavailable.")
    }

    fun onBluetoothEnableDeclined() {
        state = state.copy(isScanning = false, message = "Turn on Bluetooth to scan.")
    }

    fun onBluetoothEnableRequestFailed() {
        state = state.copy(isScanning = false, message = "Open Bluetooth settings and try again.")
    }

    fun stopScan() {
        scanJob?.cancel()
        repository.stopScan()
        state = state.copy(isScanning = false)
    }

    override fun onCleared() {
        stopScan()
        super.onCleared()
    }

    private fun List<BleDevice>.upsert(device: BleDevice): List<BleDevice> {
        val index = indexOfFirst { it.address == device.address }
        val result = if (index >= 0) {
            toMutableList().also { it[index] = device }
        } else {
            this + device
        }
        return result.sortedByDescending { it.rssi }.take(40)
    }
}

class ScannerViewModelFactory(
    private val repository: BleDeviceRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ScannerViewModel(repository) as T
    }
}
