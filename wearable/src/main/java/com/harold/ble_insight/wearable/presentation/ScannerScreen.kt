package com.harold.ble_insight.wearable.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Bluetooth
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harold.ble_insight.wearable.R
import com.harold.ble_insight.wearable.domain.model.BleDevice
import java.text.DateFormat
import java.util.Date

private sealed interface Destination {
    data object Scanner : Destination
    data class Details(val address: String) : Destination
    data class Flags(val address: String) : Destination
}

@Composable
fun ScannerScreen(
    state: ScannerState,
    onStartScan: () -> Unit,
    onStopScan: () -> Unit
) {
    var destination by remember { mutableStateOf<Destination>(Destination.Scanner) }
    val device = when (val current = destination) {
        Destination.Scanner -> null
        is Destination.Details -> state.devices.firstOrNull { it.address == current.address }
        is Destination.Flags -> state.devices.firstOrNull { it.address == current.address }
    }

    BackHandler(enabled = destination != Destination.Scanner) {
        destination = when (val current = destination) {
            is Destination.Flags -> Destination.Details(current.address)
            else -> Destination.Scanner
        }
    }

    when (val current = destination) {
        Destination.Scanner -> ScanList(
            state = state,
            onStartScan = onStartScan,
            onStopScan = onStopScan,
            onDeviceClick = { destination = Destination.Details(it.address) }
        )
        is Destination.Details -> {
            if (device == null) {
                destination = Destination.Scanner
            } else {
                DeviceDetails(
                    device = device,
                    onBack = { destination = Destination.Scanner },
                    onFlagsClick = { destination = Destination.Flags(current.address) }
                )
            }
        }
        is Destination.Flags -> {
            if (device == null) {
                destination = Destination.Scanner
            } else {
                AdvertisementFlags(
                    device = device,
                    onBack = { destination = Destination.Details(current.address) }
                )
            }
        }
    }
}

@Composable
private fun ScanList(
    state: ScannerState,
    onStartScan: () -> Unit,
    onStopScan: () -> Unit,
    onDeviceClick: (BleDevice) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            MainHeader()
        }

        item {
            ScanStatusCard(
                state = state,
                onStartScan = onStartScan,
                onStopScan = onStopScan
            )
        }

        state.message?.let { message ->
            item {
                StatusMessage(message)
            }
        }

        if (state.devices.isNotEmpty()) {
            item {
                Text(
                    text = "Nearby devices · ${state.devices.size}",
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        if (state.devices.isEmpty() && !state.isScanning) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    color = MaterialTheme.colorScheme.surfaceContainer
                ) {
                    Text(
                        text = "Tap Start Scan to discover nearby BLE devices.",
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        items(state.devices, key = { it.address }) { device ->
            DeviceRow(device = device, onClick = { onDeviceClick(device) })
        }
    }
}

@Composable
private fun MainHeader() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_ble_insight_logo),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(34.dp)
            )
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = "BLE Insight",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 18.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Text(
                    text = "Nearby BLE devices",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp,
                    lineHeight = 14.sp,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun ScanStatusCard(
    state: ScannerState,
    onStartScan: () -> Unit,
    onStopScan: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (state.isScanning) {
                CircularProgressIndicator(
                    modifier = Modifier.size(42.dp),
                    strokeWidth = 4.dp,
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceContainerHighest
                )
                Text(
                    text = "Scanning nearby devices",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 17.sp,
                    lineHeight = 21.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    maxLines = 2
                )
                Text(
                    text = "${state.devices.size} devices found",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp,
                    lineHeight = 16.sp
                )
                FilledTonalButton(
                    onClick = onStopScan,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Rounded.Stop, null, Modifier.size(17.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Stop Scan", fontSize = 13.sp)
                }
            } else {
                Surface(
                    modifier = Modifier.size(24.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Rounded.Bluetooth,
                            null,
                            modifier = Modifier.size(13.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
                Text(
                    text = "Ready to scan",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 14.sp,
                    lineHeight = 17.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Text(
                    text = "Find nearby Bluetooth Low Energy devices.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    textAlign = TextAlign.Center
                )
                Button(
                    onClick = onStartScan,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 10.dp)
                ) {
                    Icon(Icons.Rounded.PlayArrow, null, Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "Start Scan",
                        fontSize = 12.sp,
                        lineHeight = 14.sp,
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusMessage(message: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.errorContainer
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                Icons.Rounded.Warning,
                null,
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.onErrorContainer
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = message,
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onErrorContainer,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
private fun DeviceRow(device: BleDevice, onClick: () -> Unit) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Rounded.Bluetooth,
                            null,
                            Modifier.size(21.dp),
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        text = device.name,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 14.sp,
                        lineHeight = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = device.address,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 10.sp,
                        lineHeight = 13.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(Modifier.width(6.dp))
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${device.rssi} dBm",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                    Icon(
                        Icons.Rounded.ChevronRight,
                        null,
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                DeviceInfoPill("Signal", device.signalLabel())
                DeviceInfoPill("Type", device.type)
                DeviceInfoPill("Bond", device.bondState)
            }
        }
    }
}

@Composable
private fun DeviceInfoPill(label: String, value: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(
                text = label.uppercase(),
                color = MaterialTheme.colorScheme.primary,
                fontSize = 9.sp,
                lineHeight = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.6.sp
            )
            Text(
                text = value,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 12.sp,
                lineHeight = 15.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun DeviceDetails(
    device: BleDevice,
    onBack: () -> Unit,
    onFlagsClick: () -> Unit
) {
    val details: List<Pair<String, String>> = listOf(
        "Device Type" to device.type,
        "Address Type" to device.addressType,
        "Advertising" to device.advertisingType,
        "Primary PHY" to device.primaryPhy,
        "Secondary PHY" to device.secondaryPhy,
        "TX Power" to device.txPower?.let { "$it dBm" }.orDefault(),
        "RSSI" to "${device.rssi} dBm",
        "Legacy" to device.isLegacy.yesNo(),
        "Connectable" to device.isConnectable.yesNo(),
        "Last Seen" to DateFormat.getTimeInstance(DateFormat.MEDIUM).format(Date(device.lastSeenMillis))
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item { ScreenHeader("Device Details", onBack) }
        item { DeviceSummaryCard(device) }
        item {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            ) {
                Column(Modifier.padding(14.dp)) {
                    Text(
                        text = "Device information",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    details.forEachIndexed { index, item ->
                        DetailRow(item.first, item.second)
                        if (index != details.lastIndex) {
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 6.dp),
                                color = MaterialTheme.colorScheme.outlineVariant
                            )
                        }
                    }
                }
            }
        }
        item {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onFlagsClick),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 2.dp, vertical = 14.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 4.dp, end = 18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Surface(
                            modifier = Modifier.size(34.dp),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Rounded.Info,
                                    null,
                                    Modifier.size(19.dp),
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                        Text(
                            text = "Advertisement",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 13.sp,
                            lineHeight = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Flags",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 13.sp,
                            lineHeight = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = device.advertiseFlags?.let { "Flags $it · 0x${it.toString(16).uppercase()}" } ?: "Unavailable",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp,
                            lineHeight = 14.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center
                        )
                    }
                    Icon(
                        Icons.Rounded.ChevronRight,
                        null,
                        modifier = Modifier
                            .size(22.dp)
                            .align(Alignment.CenterEnd),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
        item { DataCard("Service UUIDs", device.serviceUuids) }
        item { DataCard("Manufacturer Data", device.manufacturerData) }
        item { DataCard("Service Data", device.serviceData) }
    }
}

@Composable
private fun DeviceSummaryCard(device: BleDevice) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = device.name,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 17.sp,
                lineHeight = 21.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = device.address,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp,
                lineHeight = 14.sp,
                maxLines = 1
            )
            DeviceInfoPill("Signal", device.signalLabel())
            DeviceInfoPill("Bond", device.bondState)
        }
    }
}

@Composable
private fun AdvertisementFlags(device: BleDevice, onBack: () -> Unit) {
    val raw = device.advertiseFlags
    val flags = listOf(
        Triple("LE Limited Discoverable", 0x01, raw?.hasFlag(0x01)),
        Triple("LE General Discoverable", 0x02, raw?.hasFlag(0x02)),
        Triple("BR/EDR Not Supported", 0x04, raw?.hasFlag(0x04)),
        Triple("LE + BR/EDR Controller", 0x08, raw?.hasFlag(0x08)),
        Triple("LE + BR/EDR Host", 0x10, raw?.hasFlag(0x10))
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item { ScreenHeader("Advertisement Flags", onBack) }
        item { DeviceSummaryCard(device) }
        items(flags) { flag ->
            FlagRow(flag.first, flag.third)
        }
        item {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            ) {
                Column(Modifier.padding(14.dp)) {
                    Text(
                        "Raw Flags",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        raw?.let { "0x${it.toString(16).uppercase()} ($it)" } ?: "Unknown",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun ScreenHeader(title: String, onBack: () -> Unit) {
    val compactTitle = title == "Advertisement Flags"

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHighest
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .padding(start = 4.dp, end = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier
                    .size(29.dp)
                    .clickable(onClick = onBack),
                shape = CircleShape,
                color = Color.Transparent
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Rounded.ArrowBack,
                        "Back",
                        modifier = Modifier.size(19.dp),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            Spacer(Modifier.width(2.dp))
            Text(
                text = title,
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = if (compactTitle) 9.sp else 15.sp,
                lineHeight = if (compactTitle) 11.sp else 18.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = if (compactTitle) (-0.35).sp else 0.sp,
                textAlign = TextAlign.Center,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Clip
            )
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Text(
            text = label.uppercase(),
            color = MaterialTheme.colorScheme.primary,
            fontSize = 9.sp,
            lineHeight = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.6.sp
        )
        Text(
            text = value,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun DataCard(title: String, values: List<String>) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Column(Modifier.padding(14.dp)) {
            Text(
                title,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(6.dp))
            if (values.isEmpty()) {
                Text(
                    "No data",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                )
            } else {
                values.forEach {
                    Text(
                        it,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp,
                        lineHeight = 15.sp,
                        maxLines = 6,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun FlagRow(label: String, value: Boolean?) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(28.dp),
                shape = CircleShape,
                color = when (value) {
                    true -> Color(0xFF214D3A)
                    false -> MaterialTheme.colorScheme.surfaceContainerHighest
                    null -> Color(0xFF4D4021)
                }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = when (value) {
                            true -> Icons.Rounded.Check
                            false -> Icons.Rounded.Close
                            null -> Icons.Rounded.Info
                        },
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = label,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = when (value) {
                        true -> "Supported / Set"
                        false -> "Not supported / Not set"
                        null -> "Unknown"
                    },
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 10.sp,
                    lineHeight = 13.sp
                )
            }
        }
    }
}

private fun BleDevice.signalLabel(): String = when {
    rssi >= -55 -> "Excellent"
    rssi >= -70 -> "Good"
    rssi >= -85 -> "Fair"
    else -> "Weak"
}

private fun Boolean.yesNo(): String = if (this) "Yes" else "No"

private fun String?.orDefault(): String = this ?: "N/A"

private fun Int.hasFlag(mask: Int): Boolean = this and mask != 0
