# BLE Insight

BLE Insight is a multi-module Android Bluetooth Low Energy scanner by Harold Paulino.

## Modules

- `phone`: Full Android phone experience with scanning, device details, advertisement flags, service UUIDs, manufacturer data, and service data.
- `wearable`: Compact AOSP watch experience with battery-conscious BLE scanning, RSSI sorting, device details, and advertisement flag inspection.

## Wearable efficiency

The wearable scanner uses balanced scan mode, delayed batch delivery, a 30-second scan window, and a capped in-memory device list. Scanning stops when the activity leaves the foreground.

## Build

Open the root project in Android Studio and select either the `phone` or `wearable` run configuration.

## Owner

by Harold Paulino
