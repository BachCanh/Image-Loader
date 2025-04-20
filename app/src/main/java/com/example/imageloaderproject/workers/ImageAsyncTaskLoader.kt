package com.example.imageloaderproject

import android.content.AsyncTaskLoader
import android.content.Context
import kotlinx.coroutines.runBlocking
import java.io.IOException

class ImageAsyncTaskLoader(
    context: Context,
    private val imageUrl: String
) : AsyncTaskLoader<Pair<String, String>>(context) {

    private val repository = ImageRepository()

    override fun loadInBackground(): Pair<String, String> {
        return try {
            val result = runBlocking {
                repository.fetchImage(imageUrl)
            }
            Pair(result, "")
        } catch (e: IOException) {
            Pair("", "Failed to load image: ${e.message}")
        }
    }

    // Custom method to load and deliver result since we're not using LoaderManager
    fun loadInBackground(callback: (Pair<String, String>) -> Unit) {
        Thread {
            val result = loadInBackground()
            callback(result)
        }.start()
    }

    override fun onStartLoading() {
        forceLoad()
    }
}
