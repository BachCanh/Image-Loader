// ImageLoaderViewModel.kt
package com.example.imageloaderproject

import android.content.Context
import android.os.AsyncTask
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.URL

class ImageLoaderViewModel : ViewModel() {
    var imageUrl by mutableStateOf("")
    var imageUri by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf("")

    private val repository = ImageRepository()

    // Modern approach using coroutines
    suspend fun loadImage(context: Context) {
        if (imageUrl.isEmpty()) return

        isLoading = true
        errorMessage = ""

        try {
            val result = repository.fetchImage(imageUrl)
            imageUri = result
            errorMessage = ""
        } catch (e: IOException) {
            errorMessage = "Failed to load image: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    // Traditional approach using AsyncTask
    fun loadImageWithAsyncTask(context: Context) {
        if (imageUrl.isEmpty()) return

        isLoading = true
        errorMessage = ""

        ImageLoadAsyncTask {
            imageUri = it.first
            errorMessage = it.second
            isLoading = false
        }.execute(imageUrl)
    }

    // AsyncTaskLoader approach
    fun loadImageWithAsyncTaskLoader(context: Context) {
        if (imageUrl.isEmpty()) return

        isLoading = true
        errorMessage = ""

        val loader = ImageAsyncTaskLoader(context, imageUrl)
        loader.loadInBackground {
            viewModelScope.launch {
                imageUri = it.first
                errorMessage = it.second
                isLoading = false
            }
        }
    }

    // Using WorkManager (modern replacement for AsyncTask)
    fun loadImageWithWorkManager(context: Context) {
        if (imageUrl.isEmpty()) return

        isLoading = true
        errorMessage = ""

        val imageWorkRequest = OneTimeWorkRequestBuilder<ImageLoadWorker>()
            .setInputData(workDataOf("image_url" to imageUrl))
            .build()

        WorkManager.getInstance(context).enqueue(imageWorkRequest)

        // Observe the work status
        WorkManager.getInstance(context).getWorkInfoByIdLiveData(imageWorkRequest.id)
            .observeForever { workInfo ->
                if (workInfo != null && workInfo.state.isFinished) {
                    isLoading = false
                    val outputUri = workInfo.outputData.getString("image_uri")
                    val outputError = workInfo.outputData.getString("error_message")

                    if (outputUri != null && outputUri.isNotEmpty()) {
                        imageUri = outputUri
                        errorMessage = ""
                    } else {
                        errorMessage = outputError ?: "Unknown error"
                    }
                }
            }
    }
}

// Legacy AsyncTask implementation
class ImageLoadAsyncTask(private val callback: (Pair<String, String>) -> Unit) :
    AsyncTask<String, Void, Pair<String, String>>() {

    override fun doInBackground(vararg params: String): Pair<String, String> {
        val url = params[0]
        return try {
            // In a real app, we would download and save the image
            // For demonstration, we just return the URL
            Pair(url, "")
        } catch (e: Exception) {
            Pair("", "Failed to load image: ${e.message}")
        }
    }

    override fun onPostExecute(result: Pair<String, String>) {
        callback(result)
    }
}