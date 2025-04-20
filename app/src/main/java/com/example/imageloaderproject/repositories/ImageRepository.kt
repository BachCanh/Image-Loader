package com.example.imageloaderproject

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.URL

class ImageRepository {
    suspend fun fetchImage(url: String): String {
        return withContext(Dispatchers.IO) {
            try {
                // In a real app, we would download and save the image
                // For this demo, we just validate the URL and return it
                URL(url).openConnection().connect()
                url // Return the URL as image URI
            } catch (e: IOException) {
                throw IOException("Invalid URL or network issue", e)
            }
        }
    }
}
