package com.hnimrod.apngsample

import android.content.Context
import android.os.Handler
import android.os.Looper
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import java.io.File
import java.io.IOException

/**
 * `Downloader` is a utility class for downloading files from a given URL and saving them to the application's cache directory.
 *
 * @property cacheDir The cache directory where downloaded files will be saved.
 * @property handler A handler associated with the main thread, used for posting results back to the UI thread.
 *
 * @param context The application context, used to access the cache directory.
 */
class Downloader(context: Context){

    /**
     * Callback interface for handling download results.
     */
    interface Callback {

        /**
         * Called when the download is successful.
         *
         * @param file The downloaded file, or null if the response body was empty.
         */
        fun onSuccess(file: File?)

        /**
         * Called when the download fails.
         *
         * @param throwable The exception that caused the failure.
         */
        fun onFail(throwable: Throwable) {}
    }

    private val cacheDir = context.cacheDir
    private val handler = Handler(Looper.getMainLooper())

    /**
     * Initiates a download from the specified URL and notifies the result through the provided callback.
     *
     * @param url The URL to download the file from.
     * @param callback The callback to notify the result of the download.
     */
    fun download(url: String, callback: Callback) {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder().addInterceptor(loggingInterceptor).build()
        val request = Request.Builder().url(url).build()
        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onResponse(call: Call, response: Response) {
                val body = response.body ?: kotlin.run {
                    handler.post {
                        callback.onSuccess(null)
                    }
                    return
                }

                val file = File("${cacheDir.absolutePath}/${FILE_NAME}")
                file.outputStream().use { fileOut ->
                    body.byteStream().use { bodyIn ->
                        bodyIn.copyTo(fileOut)
                    }
                }
                handler.post {
                    callback.onSuccess(file)
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                handler.post {
                    callback.onFail(e)
                }
            }
        })
    }

    companion object {
        /**
         * The name of the file where the downloaded content will be saved.
         */
        private const val FILE_NAME = "__sample.png"
    }
}