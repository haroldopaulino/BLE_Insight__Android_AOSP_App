package com.harold.ble_insight.presentation.scanner

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.HelpOutline
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.harold.ble_insight.domain.model.BleDevice

private data class FlagDefinition(val mask: Int, val title: String)

private val flagDefinitions = listOf(
    FlagDefinition(0x01, "LE Limited Discoverable Mode"),
    FlagDefinition(0x02, "LE General Discoverable Mode"),
    FlagDefinition(0x04, "BR/EDR Not Supported"),
    FlagDefinition(0x08, "Simultaneous LE and BR/EDR Controller"),
    FlagDefinition(0x10, "Simultaneous LE and BR/EDR Host")
)

private enum class FlagStatus {
    Set,
    NotSet,
    Unknown
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdvertisementFlagsScreen(device: BleDevice, onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Advertisement Flags") },
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
            item {
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(device.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text(device.address, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        flagDefinitions.forEach { definition ->
                            FlagRow(definition, device.advertiseFlags)
                        }
                    }
                }
            }
            item {
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
                        Text("Raw Flags", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Text(
                            device.advertiseFlags?.let {
                                "0x${it.toString(16).uppercase().padStart(2, '0')} ($it)"
                            } ?: "Unavailable",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
            item {
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("Legend", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        LegendRow("Supported / Set", FlagStatus.Set)
                        LegendRow("Not Supported / Not Set", FlagStatus.NotSet)
                        LegendRow("Unknown", FlagStatus.Unknown)
                    }
                }
            }
        }
    }
}

@Composable
private fun FlagRow(definition: FlagDefinition, rawFlags: Int?) {
    val status = when {
        rawFlags == null -> FlagStatus.Unknown
        rawFlags and definition.mask != 0 -> FlagStatus.Set
        else -> FlagStatus.NotSet
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatusIcon(status)
        Text(definition.title, modifier = Modifier.weight(1f))
        StatusIcon(status)
    }
}

@Composable
private fun LegendRow(label: String, status: FlagStatus) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatusIcon(status)
        Text(label)
    }
}

@Composable
private fun StatusIcon(status: FlagStatus) {
    val color = when (status) {
        FlagStatus.Set -> MaterialTheme.colorScheme.tertiary
        FlagStatus.NotSet -> MaterialTheme.colorScheme.outline
        FlagStatus.Unknown -> MaterialTheme.colorScheme.secondary
    }
    val icon = when (status) {
        FlagStatus.Set -> Icons.Rounded.Check
        FlagStatus.NotSet -> Icons.Rounded.Close
        FlagStatus.Unknown -> Icons.Rounded.HelpOutline
    }
    Surface(
        modifier = Modifier.size(26.dp),
        shape = CircleShape,
        color = color.copy(alpha = 0.16f)
    ) {
        Icon(icon, null, Modifier.padding(5.dp), tint = color)
    }
}
