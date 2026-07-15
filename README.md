# BLE Insight Android Scanner

**Professional Android Bluetooth Low Energy scanner built with Kotlin, Jetpack Compose, Material 3, clean architecture, real-time BLE discovery, RSSI sorting, advertisement inspection, permission handling, and local-only device analysis.**

BLE Insight is a native Android application for discovering and inspecting nearby Bluetooth Low Energy devices. The app is designed as a practical engineering tool for mobile, embedded, IoT, firmware, QA, and connected-device workflows where BLE visibility, advertisement validation, and signal behavior matter.

The project demonstrates modern Android development with Kotlin, clean separation of data/domain/presentation layers, reactive BLE scan results, Material 3 UI, runtime Bluetooth/location permission handling, and privacy-conscious local processing.

<img width="1536" height="1024" alt="ble_insight_android" src="https://github.com/user-attachments/assets/d27f5ef9-8b6b-4fb2-85f6-7756542cca0a" />

---

## Why This Project Matters

Bluetooth Low Energy is used in wearables, health devices, medical peripherals, smart locks, sensors, asset trackers, headphones, smart home hardware, industrial devices, and embedded IoT products. A BLE scanner is a practical engineering tool because it helps developers verify whether nearby devices are advertising correctly and whether Android can discover and inspect them reliably.

This project demonstrates:

- Kotlin Android development
- Jetpack Compose UI
- Material 3 dark interface
- Android Bluetooth Low Energy scanning
- Real-time nearby-device discovery
- RSSI and signal-strength sorting
- BLE device name, address, type, and bond-state display
- PHY and connection-detail visibility
- Service UUID inspection
- Manufacturer data inspection
- Service data inspection
- Advertisement flags inspection
- Runtime Bluetooth, nearby-device, and location permission handling
- Bluetooth enable request before scanning
- Clean data/domain/presentation architecture
- Local-only processing with no scan-result upload

---

## Project Overview

BLE Insight scans nearby Bluetooth Low Energy advertisements and displays discovered devices in a clean Android UI.

The app helps engineers answer practical questions such as:

- Is the BLE peripheral advertising?
- What RSSI value is Android receiving?
- Is the device connectable?
- What services are being advertised?
- Does the advertisement include manufacturer data?
- Does the advertisement include service data?
- What advertisement flags are present?
- Is the device bonded?
- What type of Bluetooth device is Android reporting?
- Is Bluetooth enabled and are permissions correctly granted?

This makes BLE Insight useful for Android developers, embedded engineers, firmware engineers, QA testers, and IoT product teams.

---

## Main Features

### Real-Time BLE Scanning

The app discovers nearby BLE devices in real time and updates the UI as scan results arrive.

This supports practical BLE validation scenarios such as:

- Testing embedded BLE advertisements
- Checking whether a device is discoverable
- Verifying advertising behavior after firmware changes
- Observing RSSI changes while moving a device
- Confirming whether expected service UUIDs are present
- Comparing multiple nearby BLE peripherals

### RSSI Sorting and Signal Visibility

BLE Insight displays signal strength and supports RSSI-based sorting. This helps identify nearby devices quickly and makes field testing easier when several BLE devices are advertising at the same time.

### Device Metadata Display

The app shows useful Bluetooth device details, including:

- Device name
- Device address
- Device type
- Bond state
- PHY details where available
- Connection-related advertisement details

### Service UUID Inspection

The app displays advertised service UUIDs, which is important when validating custom firmware, BLE profiles, or expected device capabilities.

### Manufacturer and Service Data Inspection

BLE Insight inspects manufacturer data and service data from advertisement packets. These fields are commonly used by embedded products, sensors, beacons, and proprietary BLE devices.

### Advertisement Flags Screen

The app includes a dedicated advertisement flags screen, making it easier to understand the low-level advertising information exposed by Android scan results.

### Runtime Permission Handling

The app handles runtime Bluetooth, nearby-device, and location permissions required by Android BLE scanning behavior.

### Bluetooth Enable Request

Before scanning, the app requests that Bluetooth be enabled when needed. This improves the user experience and makes the scanner easier to use during testing.

### Privacy-Conscious Local Processing

BLE Insight processes scan results locally. It does not upload or share scanned device information.

This is important because BLE scan results can reveal nearby devices and should be treated carefully in diagnostic tools.

---

## Technical Stack

| Area | Technology |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose |
| Design system | Material 3 |
| Bluetooth | Android BLE scanning APIs |
| Architecture | Data / Domain / Presentation separation |
| Theme | Dark Material 3 |
| Permissions | Bluetooth, nearby-device, and location runtime permissions |
| Minimum Android version | Android 8.0 or newer |
| Android SDK | 36 |
| JDK | 21 |
| Build tooling | Android Gradle Plugin 9.2 support |
| License | GPL-3.0 |

---

## Architecture

The project separates Bluetooth access, repository logic, domain models, and Compose presentation code.

```text
BLE_Insight__Android_App/
├── app/
│   └── src/main/
│       └── java/
│           └── ...
│               ├── data/
│               │   ├── ble/
│               │   └── repository/
│               ├── domain/
│               │   ├── model/
│               │   └── repository/
│               ├── presentation/
│               │   └── scanner/
│               └── ui/
│                   └── theme/
├── gradle/
├── build.gradle.kts
├── settings.gradle.kts
├── README.md
└── LICENSE
```

### `data/ble`

Contains the Android Bluetooth scanning implementation and platform-specific BLE access.

### `data/repository`

Contains repository implementation code that bridges platform BLE scan results into the app domain.

### `domain/model`

Contains BLE device models used by the application.

### `domain/repository`

Contains repository contracts that keep the domain layer separated from platform implementation details.

### `presentation/scanner`

Contains scanner screens, device details screens, and advertisement flags UI.

### `ui/theme`

Contains the dark Material 3 app theme.

---

## Data Flow

```text
Android BLE scan result
      ↓
data/ble scanner implementation
      ↓
repository mapping
      ↓
domain BLE device model
      ↓
scanner UI state
      ↓
Jetpack Compose device list and detail screens
```

This keeps Bluetooth implementation details away from the UI and makes the app easier to maintain and extend.

---

## BLE Data Displayed

BLE Insight can display or inspect:

| Data | Why it matters |
|---|---|
| Device name | Helps identify known peripherals |
| Device address | Useful for debugging repeated discoveries |
| Device type | Shows how Android classifies the device |
| Bond state | Helps confirm pairing/bonding status |
| RSSI | Indicates signal strength |
| PHY details | Useful for Bluetooth radio capability debugging |
| Service UUIDs | Confirms advertised BLE services |
| Manufacturer data | Common in proprietary BLE devices and beacons |
| Service data | Useful for device-specific payloads |
| Advertisement flags | Shows low-level advertising metadata |

---

## Android Permissions

BLE scanning requires different permissions depending on Android version.

BLE Insight handles runtime permission flows for:

- Bluetooth scanning
- Nearby-device access
- Location access where required by Android BLE behavior

The app also requests Bluetooth to be enabled before scanning.

---

## Embedded, Firmware, and IoT Relevance

This project is especially valuable for embedded and firmware workflows because BLE scanner apps are commonly used to validate wireless behavior from hardware prototypes.

A firmware or embedded engineer can use BLE Insight to verify:

- Whether the device is advertising
- Whether Android can discover the peripheral
- Whether the RSSI is stable or changing unexpectedly
- Whether expected service UUIDs are present
- Whether manufacturer data is included
- Whether service data is included
- Whether advertisement flags match expectations
- Whether bonding state is correct
- Whether Bluetooth permissions or platform behavior are blocking discovery

This makes the app a strong bridge between Android development and embedded BLE product validation.

---

## How to Build

1. Clone the repository.
2. Open the project in Android Studio with AGP 9.2 support.
3. Use JDK 21.
4. Install Android SDK 36.
5. Build and run the app on a physical Android device running Android 8.0 or newer.
6. Grant Bluetooth, nearby-device, and location permissions when requested.
7. Enable Bluetooth if prompted.
8. Start scanning to inspect nearby BLE devices.

BLE scanning should be tested on a physical Android device because emulators do not provide reliable access to real nearby BLE advertisements.

---

## License

This project is licensed under the GPL-3.0 license.


## Owner

by Harold Paulino
