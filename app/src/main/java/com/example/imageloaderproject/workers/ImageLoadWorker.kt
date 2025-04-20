package com.example.imageloaderproject

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.URL

class ImageLoadWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val imageUrl = inputData.getString("image_url") ?: return Result.failure()

        return withContext(Dispatchers.IO) {
            try {
                // In a real app, we would download and save the image
                // For this demo, we just validate the URL and return it
                URL(imageUrl).openConnection().connect()
                Result.success(workDataOf("image_uri" to imageUrl))
            } catch (e: Exception) {
                Result.failure(workDataOf("error_message" to "Failed to load image: ${e.message}"))
            }
        }
    }
}
