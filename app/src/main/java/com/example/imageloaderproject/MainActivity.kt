package com.example.imageloaderproject

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.work.*
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    private lateinit var connectivityReceiver: ConnectivityReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the broadcast receiver
        connectivityReceiver = ConnectivityReceiver()

        // Register the receiver to listen for connectivity changes
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(connectivityReceiver, filter)

        // Start the background service
        val serviceIntent = Intent(this, ImageLoaderService::class.java)
        startService(serviceIntent)

        // Setup the Compose UI
        setContent {
            ImageLoaderApp()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Unregister the receiver when the activity is destroyed
        unregisterReceiver(connectivityReceiver)
    }
}

@Composable
fun ImageLoaderApp() {
    val viewModel: ImageLoaderViewModel = viewModel()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Monitor network connectivity
    val isConnected = remember { mutableStateOf(isNetworkAvailable(context)) }

    // Update connectivity state when broadcast receiver notifies
    DisposableEffect(context) {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: android.net.Network) {
                isConnected.value = true
            }

            override fun onLost(network: android.net.Network) {
                isConnected.value = false
            }
        }

        connectivityManager.registerDefaultNetworkCallback(callback)

        onDispose {
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }

    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // URL Input Field
                OutlinedTextField(
                    value = viewModel.imageUrl,
                    onValueChange = { viewModel.imageUrl = it },
                    label = { Text("Image URL") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                    enabled = isConnected.value
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Load Image Button
                Button(
                    onClick = {
                        scope.launch {
                            viewModel.loadImage(context)
                        }
                    },
                    enabled = isConnected.value && viewModel.imageUrl.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Load Image")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Network Status
                if (!isConnected.value) {
                    Text(
                        text = "No internet connection",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(8.dp)
                    )
                }

                // Loading Status
                if (viewModel.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.padding(8.dp))
                    Text(text = "Loading...", modifier = Modifier.padding(8.dp))
                }

                // Error Message
                if (viewModel.errorMessage.isNotEmpty()) {
                    Text(
                        text = viewModel.errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(8.dp)
                    )
                }

                // Image Display
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (viewModel.imageUri.isNotEmpty()) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(viewModel.imageUri)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Loaded Image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    }
                }

                // Button to test Async Task implementation
                Button(
                    onClick = {
                        viewModel.loadImageWithAsyncTask(context)
                    },
                    enabled = isConnected.value && viewModel.imageUrl.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Load with AsyncTask")
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Button to test AsyncTaskLoader implementation
                Button(
                    onClick = {
                        viewModel.loadImageWithAsyncTaskLoader(context)
                    },
                    enabled = isConnected.value && viewModel.imageUrl.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Load with AsyncTaskLoader")
                }
            }
        }
    }
}

// Helper function to check if network is available
fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork
    val capabilities = connectivityManager.getNetworkCapabilities(network)
    return capabilities != null
}
