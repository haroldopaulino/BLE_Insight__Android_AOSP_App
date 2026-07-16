package com.harold.ble_insight.presentation.scanner

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.BluetoothConnected
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Devices
import androidx.compose.material.icons.rounded.SignalCellularAlt
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.harold.ble_insight.domain.model.BleDevice
import java.text.DateFormat
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceDetailsScreen(
    device: BleDevice,
    onBack: () -> Unit,
    onFlagsClick: () -> Unit
) {
    val details: List<Pair<String, String>> = listOf(
        "Device Type" to device.type,
        "Address Type" to device.addressType,
        "Advertising Type" to device.advertisingType,
        "Primary PHY" to device.primaryPhy,
        "Secondary PHY" to device.secondaryPhy,
        "TX Power" to (device.txPower?.let { "$it dBm" } ?: "N/A"),
        "RSSI" to "${device.rssi} dBm",
        "Legacy Advertising" to if (device.isLegacy) "Yes" else "No",
        "Connectable" to if (device.isConnectable) "Yes" else "No",
        "Last Seen" to DateFormat.getTimeInstance(DateFormat.MEDIUM).format(Date(device.lastSeenMillis))
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Device Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { DeviceHeaderCard(device) }
            item { DetailSection("Device information", details) }
            item { AdvertisementFlagsButton(device, onFlagsClick) }
            item {
                ValueListSection(
                    title = "Service UUIDs",
                    values = device.serviceUuids,
                    emptyMessage = "No service UUIDs found"
                )
            }
            item {
                ValueListSection(
                    title = "Manufacturer Data",
                    values = device.manufacturerData,
                    emptyMessage = "No manufacturer data"
                )
            }
            item {
                ValueListSection(
                    title = "Service Data",
                    values = device.serviceData,
                    emptyMessage = "No service data"
                )
            }
        }
    }
}

@Composable
private fun DeviceHeaderCard(device: BleDevice) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(58.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Rounded.BluetoothConnected, null, Modifier.size(30.dp))
                    }
                }
                Spacer(Modifier.width(14.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        device.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(device.address, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("${device.rssi} dBm", fontWeight = FontWeight.Bold)
                    Text(device.signalLabel(), color = MaterialTheme.colorScheme.primary)
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AssistChip(
                    onClick = {},
                    label = { Text(device.bondState) },
                    leadingIcon = { Icon(Icons.Rounded.Devices, null, Modifier.size(18.dp)) }
                )
                AssistChip(
                    onClick = {},
                    label = { Text(device.signalLabel()) },
                    leadingIcon = { Icon(Icons.Rounded.SignalCellularAlt, null, Modifier.size(18.dp)) }
                )
            }
        }
    }
}

@Composable
private fun DetailSection(title: String, rows: List<Pair<String, String>>) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(13.dp)
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            rows.forEach { (label, value) ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        label,
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        value,
                        modifier = Modifier.weight(1f),
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.End
                    )
                }
            }
        }
    }
}

@Composable
private fun AdvertisementFlagsButton(device: BleDevice, onClick: () -> Unit) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        TextButton(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(18.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    "Advertisement Flags",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    device.advertiseFlags?.let {
                        "Flags $it · 0x${it.toString(16).uppercase().padStart(2, '0')}"
                    } ?: "Flags unavailable",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(Icons.Rounded.ChevronRight, contentDescription = "Open advertisement flags")
        }
    }
}

@Composable
private fun ValueListSection(title: String, values: List<String>, emptyMessage: String) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            if (values.isEmpty()) {
                Text(emptyMessage, color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                values.forEach { value ->
                    Text(value, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}
