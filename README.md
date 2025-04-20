### Student Name: Bạch Đức Cảnh
### Student ID: 22110012
### Project: Image Loader app

# Image Loader Android App

An Android application that demonstrates various asynchronous programming techniques in Android, including AsyncTask, AsyncTaskLoader, WorkManager, and background services.

## Features

- Load images from URLs
- Multiple image loading implementations:
  - Modern approach using Coroutines
  - Traditional approach using AsyncTask
  - Legacy approach using AsyncTaskLoader
  - Modern approach using WorkManager
- Network connectivity monitoring
- Background service with periodic notifications
- Jetpack Compose UI

## Requirements

- Android Studio Arctic Fox or newer
- Minimum SDK: API 24 (Android 7.0 Nougat)
- Target SDK: API 34

## Setup Instructions

1. Clone the repository
2. Open the project in Android Studio
3. Build and run the app

## Implementation Details

### AsyncTask Implementation

The app demonstrates the traditional AsyncTask approach through the `ImageLoadAsyncTask` class. Although AsyncTask is deprecated in newer Android versions, it shows the legacy way of handling background operations. The AsyncTask:

- Takes a URL as input
- Processes the image loading in the background
- Returns the result or error message
- Updates the UI on the main thread

### AsyncTaskLoader Implementation

The AsyncTaskLoader implementation is shown in the `ImageAsyncTaskLoader` class. This demonstrates the framework's loader pattern designed to handle configuration changes better than AsyncTask. The AsyncTaskLoader:

- Persists across configuration changes
- Loads data in the background
- Delivers results to the UI thread
- Handles the loader lifecycle

### Network Connectivity Monitoring

The app monitors network connectivity changes using:

- BroadcastReceiver for CONNECTIVITY_ACTION events
- ConnectivityManager to check network status
- UI updates based on connectivity state

### Background Service with Notifications

The `ImageLoaderService` demonstrates:

- A foreground service running in the background
- Periodic notifications every 5 minutes
- Proper notification channel creation for Android 8.0+
- Making the app open when the notification is clicked

### Modern Approaches

The app also includes modern Android development patterns:

- Jetpack Compose for UI
- MVVM architecture with ViewModel
- Coroutines for asynchronous operations
- WorkManager as a modern replacement for AsyncTask

## Permissions

The app requires the following permissions:

- `INTERNET`: For loading images from URLs
- `ACCESS_NETWORK_STATE`: For monitoring network connectivity
- `FOREGROUND_SERVICE`: For running the background service

## Architecture

The app follows the MVVM (Model-View-ViewModel) architecture pattern:

- **View**: MainActivity with Jetpack Compose UI
- **ViewModel**: ImageLoaderViewModel
- **Model**: ImageRepository

## Libraries Used

- Jetpack Compose for UI
- Lifecycle components for ViewModel
- Coil for image loading
- WorkManager for background work
- Core-KTX for Kotlin extensions
