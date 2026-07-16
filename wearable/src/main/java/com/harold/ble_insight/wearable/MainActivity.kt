package com.harold.ble_insight.wearable

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.harold.ble_insight.wearable.data.ble.AndroidBleScanner
import com.harold.ble_insight.wearable.data.repository.BleDeviceRepositoryImpl
import com.harold.ble_insight.wearable.presentation.ScannerScreen
import com.harold.ble_insight.wearable.presentation.ScannerViewModel
import com.harold.ble_insight.wearable.presentation.ScannerViewModelFactory
import com.harold.ble_insight.wearable.ui.theme.BleInsightWearableTheme

class MainActivity : ComponentActivity() {

    private val permissions by lazy { BlePermissionProvider.requiredPermissions() }

    private val bluetoothAdapter by lazy {
        (getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
    }

    private val viewModel: ScannerViewModel by viewModels {
        ScannerViewModelFactory(
            BleDeviceRepositoryImpl(AndroidBleScanner(applicationContext))
        )
    }

    private var pendingScan = false

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { grants ->
        val granted = grants.values.all { it }
        viewModel.onPermissionResult(granted)
        if (granted && pendingScan) {
            pendingScan = false
            startScanOrRequestBluetooth()
        } else if (!granted) {
            pendingScan = false
        }
    }

    private val enableBluetoothLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (bluetoothAdapter?.isEnabled == true) {
            viewModel.startScan()
        } else {
            viewModel.onBluetoothEnableDeclined()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BleInsightWearableTheme {
                ScannerScreen(
                    state = viewModel.state,
                    onStartScan = {
                        if (BlePermissionProvider.hasPermissions(this, permissions)) {
                            startScanOrRequestBluetooth()
                        } else {
                            pendingScan = true
                            permissionLauncher.launch(permissions)
                        }
                    },
                    onStopScan = viewModel::stopScan
                )
            }
        }
    }

    override fun onStop() {
        viewModel.stopScan()
        super.onStop()
    }

    private fun startScanOrRequestBluetooth() {
        val adapter = bluetoothAdapter
        if (adapter == null) {
            viewModel.onBluetoothUnavailable()
            return
        }
        if (adapter.isEnabled) {
            viewModel.startScan()
        } else {
            runCatching {
                enableBluetoothLauncher.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
            }.onFailure {
                viewModel.onBluetoothEnableRequestFailed()
            }
        }
    }
}
