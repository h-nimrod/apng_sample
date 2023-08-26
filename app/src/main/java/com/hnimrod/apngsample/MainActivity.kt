package com.hnimrod.apngsample

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.hnimrod.apngsample.databinding.ActivityMainBinding
import com.linecorp.apng.ApngDrawable
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var drawable: ApngDrawable? = null
    private val buttons by lazy { listOf(binding.button1, binding.button2, binding.button3, binding.button4) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        PRDownloader.initialize(applicationContext)

        binding.button1.setOnClickListener { loadApngFromUrl("https://raw.githubusercontent.com/h-nimrod/apng_sample/master/img/sample0.png") }
        binding.button2.setOnClickListener { loadApngFromUrl("https://raw.githubusercontent.com/h-nimrod/apng_sample/master/img/sample2.png") }
        binding.button3.setOnClickListener { loadApngFromUrl("http://littlesvr.ca/apng/images/clock.png") }
        binding.button4.setOnClickListener { loadApngFromUrl("http://littlesvr.ca/apng/images/o_sample.png") }
    }

    override fun onStart() {
        super.onStart()
        loadDefaultApng()
    }

    /**
     * Loads the default APNG image from the resources and displays it in the ImageView.
     */
    private fun loadDefaultApng() {
        ApngDrawable.decode(resources, R.drawable.apng).apply {
            setTargetDensity(resources.displayMetrics)
            binding.imageView.setImageDrawable(this)
            start()
        }
    }

    /**
     * Initiates the download of an APNG image from the given URL and displays it once downloaded.
     *
     * @param url The URL of the APNG image to be downloaded.
     */
    private fun loadApngFromUrl(url: String) {
        setButtonsEnabled(false)

        val cacheDirPath = cacheDir.absolutePath
        PRDownloader.download(url, cacheDirPath, CACHE_FILE_NAME)
            .build()
            .start(object : OnDownloadListener {
                override fun onDownloadComplete() {
                    val file = File("$cacheDirPath/$CACHE_FILE_NAME")
                    displayApng(file)
                    setButtonsEnabled(true)
                }

                override fun onError(error: com.downloader.Error?) {
                    setButtonsEnabled(true)
                    val errorMessage = error?.toString() ?: "some error occurred"
                    Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_LONG).show()
                }
            })
    }

    /**
     * Decodes the provided APNG file and displays it in the ImageView.
     *
     * @param file The APNG file to be decoded and displayed.
     */
    private fun displayApng(file: File) {
        drawable?.clearAnimationCallbacks()
        drawable = ApngDrawable.decode(file).apply {
            loopCount = 3
            setTargetDensity(resources.displayMetrics)
            start()
        }
        binding.imageView.setImageDrawable(drawable)
    }

    /**
     * Enables or disables the buttons based on the provided parameter.
     *
     * @param isEnabled A boolean indicating whether the buttons should be enabled or not.
     */
    private fun setButtonsEnabled(isEnabled: Boolean) {
        buttons.forEach { it.isEnabled = isEnabled }
    }

    companion object {
        private const val CACHE_FILE_NAME = "sample.png"
    }
}
