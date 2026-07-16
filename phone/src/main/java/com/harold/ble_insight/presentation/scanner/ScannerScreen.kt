package com.harold.ble_insight.presentation.scanner

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bluetooth
import androidx.compose.material.icons.rounded.BluetoothConnected
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Devices
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.SignalCellularAlt
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.harold.ble_insight.R
import com.harold.ble_insight.domain.model.BleDevice

private sealed interface ScannerDestination {
    data object Scanner : ScannerDestination
    data class Details(val address: String) : ScannerDestination
    data class Flags(val address: String) : ScannerDestination
}

@Composable
fun ScannerScreen(
    state: ScannerState,
    onStartScan: () -> Unit,
    onStopScan: () -> Unit
) {
    var destination by remember { mutableStateOf<ScannerDestination>(ScannerDestination.Scanner) }
    val selectedDevice = when (val current = destination) {
        is ScannerDestination.Details -> state.devices.firstOrNull { it.address == current.address }
        is ScannerDestination.Flags -> state.devices.firstOrNull { it.address == current.address }
        ScannerDestination.Scanner -> null
    }

    BackHandler(enabled = destination != ScannerDestination.Scanner) {
        destination = when (destination) {
            is ScannerDestination.Flags -> ScannerDestination.Details((destination as ScannerDestination.Flags).address)
            else -> ScannerDestination.Scanner
        }
    }

    when (val current = destination) {
        ScannerDestination.Scanner -> ScannerListScreen(
            state = state,
            onStartScan = onStartScan,
            onStopScan = onStopScan,
            onDeviceClick = { destination = ScannerDestination.Details(it.address) }
        )

        is ScannerDestination.Details -> {
            if (selectedDevice == null) {
                destination = ScannerDestination.Scanner
            } else {
                DeviceDetailsScreen(
                    device = selectedDevice,
                    onBack = { destination = ScannerDestination.Scanner },
                    onFlagsClick = { destination = ScannerDestination.Flags(current.address) }
                )
            }
        }

        is ScannerDestination.Flags -> {
            if (selectedDevice == null) {
                destination = ScannerDestination.Scanner
            } else {
                AdvertisementFlagsScreen(
                    device = selectedDevice,
                    onBack = { destination = ScannerDestination.Details(current.address) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScannerListScreen(
    state: ScannerState,
    onStartScan: () -> Unit,
    onStopScan: () -> Unit,
    onDeviceClick: (BleDevice) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("BLE Insight", fontWeight = FontWeight.SemiBold)
                        Text(
                            "Nearby Bluetooth Low Energy devices",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    Surface(
                        modifier = Modifier.padding(start = 16.dp).size(46.dp),
                        shape = CircleShape,
                        color = Color.Transparent
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                painter = painterResource(R.drawable.ic_ble_insight_logo),
                                contentDescription = "BLE Insight",
                                tint = Color.Unspecified,
                                modifier = Modifier.size(44.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item { ScanControlCard(state, onStartScan, onStopScan) }
            item { ScannerStatsCard(state) }
            item {
                AnimatedVisibility(visible = state.message != null) {
                    state.message?.let { MessageCard(it) }
                }
            }

            if (state.devices.isEmpty()) {
                item { EmptyScannerCard(state.isScanning) }
            } else {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Discovered devices",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        SuggestionChip(
                            onClick = {},
                            label = { Text("${state.devices.size} found") },
                            icon = { Icon(Icons.Rounded.Devices, null, Modifier.size(18.dp)) }
                        )
                    }
                }
                items(state.devices, key = { it.address }) { device ->
                    DeviceCard(device = device, onClick = { onDeviceClick(device) })
                }
            }
        }
    }
}

@Composable
private fun ScanControlCard(
    state: ScannerState,
    onStartScan: () -> Unit,
    onStopScan: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        Column(
            modifier = Modifier.padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(64.dp),
                    shape = CircleShape,
                    color = if (state.isScanning) {
                        Color(0xFFE8E1D8)
                    } else {
                        MaterialTheme.colorScheme.primaryContainer
                    }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            painter = painterResource(
                                if (state.isScanning) {
                                    R.drawable.ic_ble_scan_active
                                } else {
                                    R.drawable.ic_ble_insight_scan
                                }
                            ),
                            contentDescription = null,
                            tint = Color.Unspecified,
                            modifier = Modifier.size(34.dp)
                        )
                    }
                }
                Spacer(Modifier.width(16.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        if (state.isScanning) "Scanning nearby devices" else "Ready to scan",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        if (state.isScanning) {
                            "Listening for BLE advertisements in real time."
                        } else {
                            "Start a scan to discover nearby BLE devices."
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            AnimatedVisibility(visible = state.isScanning) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth().height(6.dp)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onStartScan,
                    enabled = !state.isScanning,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(18.dp),
                    contentPadding = PaddingValues(vertical = 14.dp)
                ) {
                    Icon(painterResource(R.drawable.ic_ble_insight_scan), null, Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Start Scan")
                }
                FilledTonalButton(
                    onClick = onStopScan,
                    enabled = state.isScanning,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(18.dp),
                    contentPadding = PaddingValues(vertical = 14.dp)
                ) {
                    Icon(Icons.Rounded.Stop, null, Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Stop")
                }
            }
        }
    }
}

@Composable
private fun ScannerStatsCard(state: ScannerState) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(18.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem("Devices", state.devices.size.toString())
            StatItem("Strongest RSSI", state.devices.maxByOrNull { it.rssi }?.let { "${it.rssi} dBm" } ?: "--")
            StatItem("Status", if (state.isScanning) "Active" else "Idle")
        }
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun MessageCard(message: String) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(Icons.Rounded.Info, contentDescription = null)
            Text(message)
        }
    }
}

@Composable
private fun EmptyScannerCard(isScanning: Boolean) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(Icons.Rounded.Devices, null, Modifier.size(44.dp), tint = MaterialTheme.colorScheme.primary)
            Text(if (isScanning) "Waiting for advertisements" else "No devices yet", fontWeight = FontWeight.SemiBold)
            Text(
                if (isScanning) {
                    "BLE devices will appear here as advertisements are received."
                } else {
                    "Start scanning to discover nearby BLE devices."
                },
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DeviceCard(device: BleDevice, onClick: () -> Unit) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        Column {
            ListItem(
                headlineContent = {
                    Text(
                        device.name,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                supportingContent = {
                    Text(device.address, maxLines = 1, overflow = TextOverflow.Ellipsis)
                },
                leadingContent = {
                    Surface(
                        modifier = Modifier.size(52.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Rounded.BluetoothConnected, null)
                        }
                    }
                },
                trailingContent = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(horizontalAlignment = Alignment.End) {
                            Text("${device.rssi}", fontWeight = FontWeight.Bold)
                            Text("dBm", style = MaterialTheme.typography.labelSmall)
                        }
                        Spacer(Modifier.width(8.dp))
                        Icon(Icons.Rounded.ChevronRight, contentDescription = "Open device details")
                    }
                },
                colors = ListItemDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                )
            )
            HorizontalDivider(Modifier.padding(horizontal = 16.dp))
            FlowRow(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AssistChip(
                    onClick = onClick,
                    label = { Text(device.signalLabel()) },
                    leadingIcon = { Icon(Icons.Rounded.SignalCellularAlt, null, Modifier.size(18.dp)) }
                )
                AssistChip(
                    onClick = onClick,
                    label = { Text(device.type) },
                    leadingIcon = { Icon(Icons.Rounded.Bluetooth, null, Modifier.size(18.dp)) }
                )
                AssistChip(
                    onClick = onClick,
                    label = { Text(device.bondState) },
                    leadingIcon = { Icon(Icons.Rounded.Devices, null, Modifier.size(18.dp)) }
                )
                device.advertiseFlags?.let { flags ->
                    AssistChip(
                        onClick = onClick,
                        label = { Text("Flags $flags") },
                        leadingIcon = { Icon(Icons.Rounded.LocationOn, null, Modifier.size(18.dp)) }
                    )
                }
            }
        }
    }
}

fun BleDevice.signalLabel(): String {
    return when {
        rssi >= -55 -> "Excellent"
        rssi >= -70 -> "Good"
        rssi >= -85 -> "Fair"
        else -> "Weak"
    }
}
