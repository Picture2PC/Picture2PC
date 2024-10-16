# Picture2PC

Picture2PC is a Kotlin-based application that allows users to capture, process, and transmit images from an Android device to a desktop application. The project leverages various Android and desktop libraries to provide a seamless experience.

## Features

- Capture images using the device camera.
- Process images with edge detection and other enhancements.
- Transmit images to a desktop application.
- Display notifications on the desktop when images are received.

## Getting Started

### Prerequisites

- Android Studio Koala Feature Drop | 2024.1.2
- JDK 11 or higher
- Gradle 7.0 or higher

### Installation

1. Clone the repository:
    ```sh
    git clone https://github.com/yourusername/Picture2PC.git
    ```
2. Open the project in Android Studio.

3. Sync the project with Gradle files.

### Running the Application

#### Android

1. Connect your Android device or start an emulator.
2. Build and run the `android` module.

#### Desktop

1. Build and run the `desktop` module.

## Usage

### Capturing and Sending Images

1. Open the app on your Android device.
2. Use the camera to capture an image.
3. The image will be processed and sent to the desktop application.
4. A notification will appear on the desktop when the image is received.

## Code Overview

### Android

- `CameraViewModel.kt`: Manages camera operations and image transmission.
- `PictureManager.kt`: Handles image capture and processing.

### Desktop

- `PictureDisplayViewModel.kt`: Manages image display and notifications.
- `ToastNotification.kt`: Handles system tray notifications.

## Contributing

1. Fork the repository.
2. Create a new branch (`git checkout -b feature-branch`).
3. Make your changes.
4. Commit your changes (`git commit -m 'Add some feature'`).
5. Push to the branch (`git push origin feature-branch`).
6. Open a pull request.

## Acknowledgments

- Thanks to all contributors and open-source projects that made this possible.