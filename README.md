# BLE Insight

BLE Insight is a modern Android application for discovering and inspecting nearby Bluetooth Low Energy devices.

## Features

- Real-time BLE scanning
- Signal strength and RSSI sorting
- Device name, address, type, bond state, PHY and connection details
- Service UUID inspection
- Manufacturer and service data inspection
- Dedicated advertisement flags screen
- Dark Material 3 interface
- Runtime Bluetooth, nearby-device and location permission handling
- Bluetooth enable request before scanning

## Architecture

The project separates Bluetooth access, repository logic, domain models and Compose presentation code.

- `data/ble`: Android Core Bluetooth scanning
- `data/repository`: Repository implementation
- `domain/model`: BLE device models
- `domain/repository`: Repository contracts
- `presentation/scanner`: Scanner, device details and advertisement flags screens
- `ui/theme`: Dark Material 3 theme

## Requirements

- Android Studio with AGP 9.2 support
- JDK 21
- Android SDK 36
- Android 8.0 or newer

## Privacy

BLE Insight processes scan results locally. It does not upload or share scanned device information.

## Owner

by Harold Paulino
