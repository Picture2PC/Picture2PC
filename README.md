# Picture2PC

**Picture2PC** is a cross-platform application that enables seamless document scanning and transfer from Android devices to desktop computers. It uses AI-powered edge detection to automatically identify document boundaries and provides powerful image processing tools for document preparation.

## 🚀 Features

### Android App
- **Camera Integration**: Built-in camera with flash control for capturing high-quality document images
- **AI-Powered Edge Detection**: Automatic document boundary detection using YOLOv8 segmentation model
- **Real-time Preview**: Live camera preview with detected document corners overlay
- **Multi-Device Support**: Send images to multiple connected desktop clients simultaneously

### Desktop App
- **Real-time Image Reception**: Receive images from connected Android devices over local network
- **Advanced Image Processing**:
  - **Auto Contrast**: Enhance document readability automatically
  - **Smart Crop**: Crop to detected document boundaries with perspective correction
  - **Rotate**: 90-degree clockwise/counterclockwise rotation
  - **Manual Adjustment**: Fine-tune corner points for precise cropping
- **Batch Processing**: "Do All" feature for automated contrast, crop, and copy operations
- **Image Queue Management**: Handle up to 5 images in queue with easy navigation
- **System Tray Integration**: Run in background with quick access from system tray
- **Clipboard Integration**: Automatically copy processed images to clipboard

## 🏗️ Architecture

The project follows a modular architecture with three main components:

### Modules
1. **android**: Android mobile application (Jetpack Compose)
2. **desktop**: Desktop application (Compose Multiplatform for Desktop)
3. **common**: Shared networking and UI components (Kotlin Multiplatform)

### Technology Stack

#### Android
- **UI Framework**: Jetpack Compose with Material3
- **Camera**: CameraX API
- **ML/CV**: OpenCV + YOLOv8 ONNX model for document detection
- **Dependency Injection**: Koin
- **Data Storage**: DataStore Preferences

#### Desktop
- **UI Framework**: Compose Multiplatform for Desktop
- **Image Processing**: OpenCV (contrast, crop, rotation)
- **Platform**: JVM-based desktop application

#### Common (Shared)
- **Networking**: TCP/IP with multicast for device discovery
- **Serialization**: Kotlinx Serialization with CBOR
- **Coroutines**: Asynchronous networking and data flow
- **Architecture**: MVVM with StateFlow/SharedFlow

## 📋 Requirements

### Android
- Android 8.0 (API 26) or higher
- Camera permission
- Network access permission

### Desktop
- Java 8 or higher
- OpenCV native libraries
- Supported OS: Windows, Linux (Debian-based), macOS

## 🛠️ Building the Project

### Prerequisites
- JDK 8 or higher
- Gradle (included via wrapper)
- Android SDK (for Android module)

### Build Commands

#### Android APK
```bash
./gradlew :android:assembleRelease
```

#### Desktop Application
```bash
./gradlew :desktop:packageDistributionForCurrentOS
```

This will create platform-specific installers:
- Windows: `.exe` and `.msi` in `desktop/build/compose/binaries/main/`
- Linux: `.deb` in `desktop/build/compose/binaries/main/`

#### Run Desktop App (Development)
```bash
./gradlew :desktop:run
```

## 📱 Usage

### Setup

1. **Install Applications**
   - Install the Android app on your mobile device
   - Install the desktop app on your computer

2. **Network Connection**
   - Ensure both devices are connected to the same local network
   - The applications use multicast for automatic device discovery

### Workflow

#### On Android:
1. Launch the app and grant camera permissions
2. Point camera at a document - AI will automatically detect document edges
3. Tap capture button to take a photo
4. Review the detected corners (green overlay)
5. Tap send button to transmit to connected desktop(s)

#### On Desktop:
1. Launch the application (runs in system tray)
2. Open main window from system tray icon
3. Connected Android devices appear automatically
4. Received images appear in the image display area
5. Use the toolbar buttons to:
   - **Reset**: Restore original image
   - **Navigate**: Move between queued images (←/→)
   - **Rotate**: Rotate image clockwise/counterclockwise
   - **Contrast**: Auto-enhance document contrast
   - **Crop**: Apply perspective correction and crop to corners
   - **Copy**: Copy to clipboard
   - **Do All**: Run contrast + crop + copy sequence
6. Adjust corner points by clicking on the image if needed
7. Navigate between images using arrow buttons

## 🔧 Configuration

The application uses local network discovery via multicast. No manual IP configuration is required when devices are on the same network.

## 🤝 Contributing

This is an open-source project. Contributions are welcome!

## 📄 License

[Add license information here]

## 🏷️ Version

Current version: 0.1

## 🔍 Technical Details

### Network Protocol
- Uses TCP/IP for reliable image transfer
- Multicast for automatic peer discovery
- Custom binary protocol with CBOR serialization
- Supports payload types: Picture, Ping/Pong, Name updates

### Image Processing Pipeline

**Android Side:**
1. Capture image via CameraX
2. YOLOv8 model inference for document detection
3. Extract corner points from segmentation mask
4. Encode image + corners as CBOR payload
5. Send via TCP to all connected desktops

**Desktop Side:**
1. Receive CBOR payload over TCP
2. Decode image and corner points
3. Display with editable corner overlay
4. Apply transformations:
   - Contrast enhancement via OpenCV
   - Perspective transform for crop
   - 90° rotations with corner recalculation
5. Copy final result to clipboard

### Edge Detection Model
- **Model**: YOLOv8 Segmentation (ONNX format)
- **Input**: 256x256 RGB image
- **Output**: Document segmentation mask + bounding boxes
- **Post-processing**: Extract 4-point polygon from mask
