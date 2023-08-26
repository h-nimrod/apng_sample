package com.hnimrod.apngsample

import android.os.Bundle
import android.view.View
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
    private val buttons by lazy { listOf(binding.button1, binding.button2, binding.button3) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        PRDownloader.initialize(applicationContext)

        binding.button1.setOnClickListener { loadApngFromUrl("https://ics-creative.github.io/140930_apng/images/elephant_apng_zopfli.png") }
        binding.button2.setOnClickListener { loadApngFromUrl("http://littlesvr.ca/apng/images/clock.png") }
        binding.button3.setOnClickListener { loadApngFromUrl("http://littlesvr.ca/apng/images/o_sample.png") }
    }

    override fun onStart() {
        super.onStart()
        loadDefaultAPNG()
    }

    private fun loadDefaultAPNG() {
        ApngDrawable.decode(resources, R.drawable.apng).apply {
            setTargetDensity(resources.displayMetrics)
            binding.imageView.setImageDrawable(this)
            start()
        }
    }

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

    private fun displayApng(file: File) {
        drawable?.clearAnimationCallbacks()
        drawable = ApngDrawable.decode(file).apply {
            loopCount = 3
            setTargetDensity(resources.displayMetrics)
            start()
        }
        binding.imageView.setImageDrawable(drawable)
    }

    private fun setButtonsEnabled(isEnabled: Boolean) {
        buttons.forEach { it.isEnabled = isEnabled }
    }

    companion object {
        private const val CACHE_FILE_NAME = "sample.png"
    }
}
