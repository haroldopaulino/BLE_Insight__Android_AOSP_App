package com.harold.ble_insight

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import com.harold.ble_insight.data.ble.AndroidBleScanner
import com.harold.ble_insight.data.repository.BleDeviceRepositoryImpl
import com.harold.ble_insight.presentation.scanner.ScannerScreen
import com.harold.ble_insight.presentation.scanner.ScannerViewModel
import com.harold.ble_insight.presentation.scanner.ScannerViewModelFactory
import com.harold.ble_insight.ui.theme.BleInsightTheme

class MainActivity : ComponentActivity() {

    private val permissions by lazy {
        BlePermissionProvider.requiredPermissions()
    }

    private val bluetoothAdapter: BluetoothAdapter? by lazy {
        (getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
    }

    private val scannerViewModel: ScannerViewModel by viewModels {
        ScannerViewModelFactory(
            BleDeviceRepositoryImpl(
                AndroidBleScanner(applicationContext)
            )
        )
    }

    private var pendingScanRequest = false

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { grants ->
        val granted = grants.values.all { it }
        scannerViewModel.onPermissionResult(granted)
        if (granted && pendingScanRequest) {
            pendingScanRequest = false
            startScanOrRequestBluetooth()
        } else if (!granted) {
            pendingScanRequest = false
        }
    }

    private val enableBluetoothLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (isBluetoothEnabled()) {
            scannerViewModel.startScan()
        } else {
            scannerViewModel.onBluetoothEnableDeclined()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BleInsightTheme {
                LaunchedEffect(Unit) {
                    permissionLauncher.launch(permissions)
                }
                ScannerScreen(
                    state = scannerViewModel.state,
                    onStartScan = {
                        if (BlePermissionProvider.hasPermissions(this, permissions)) {
                            startScanOrRequestBluetooth()
                        } else {
                            pendingScanRequest = true
                            permissionLauncher.launch(permissions)
                        }
                    },
                    onStopScan = scannerViewModel::stopScan
                )
            }
        }
    }

    override fun onStop() {
        scannerViewModel.stopScan()
        super.onStop()
    }

    private fun startScanOrRequestBluetooth() {
        val adapter = bluetoothAdapter
        if (adapter == null) {
            scannerViewModel.onBluetoothUnavailable()
            return
        }

        if (isBluetoothEnabled()) {
            scannerViewModel.startScan()
        } else {
            runCatching {
                enableBluetoothLauncher.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
            }.onFailure {
                scannerViewModel.onBluetoothEnableRequestFailed()
            }
        }
    }

    private fun isBluetoothEnabled(): Boolean {
        return runCatching { bluetoothAdapter?.isEnabled == true }.getOrDefault(false)
    }
}
