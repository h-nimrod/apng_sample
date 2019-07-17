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
    private val buttons = mutableListOf<View>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        PRDownloader.initialize(applicationContext)

        binding.button1.setOnClickListener {
            load("https://ics-creative.github.io/140930_apng/images/elephant_apng_zopfli.png")
        }

        binding.button2.setOnClickListener {
            load("http://littlesvr.ca/apng/images/clock.png")
        }

        binding.button3.setOnClickListener {
            load("http://littlesvr.ca/apng/images/o_sample.png")
        }

        buttons.addAll(listOf(binding.button1, binding.button2, binding.button3))
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

    private fun load(url: String) {

        setEnableButtons(false)

        val CACHE_FILE_NAME = "sample.png"
        val cacheDir = cacheDir
        PRDownloader.download(url, cacheDir.absolutePath, CACHE_FILE_NAME)
            .build()
            .start(object : OnDownloadListener {
                override fun onDownloadComplete() {
                    val file = File(cacheDir.absolutePath + "/" + CACHE_FILE_NAME)
                    loadAPNG(file)
                    setEnableButtons(true)
                }

                override fun onError(error: com.downloader.Error?) {
                    setEnableButtons(true)
                    val message = error?.toString() ?: "some error occurred"
                    Toast.makeText(this@MainActivity, message, Toast.LENGTH_LONG).show()
                }
            })
    }

    private fun loadAPNG(file: File) {
        drawable?.clearAnimationCallbacks()
        drawable = ApngDrawable.decode(file).apply {
            loopCount = 3
            setTargetDensity(resources.displayMetrics)
            start()
        }
        binding.imageView.setImageDrawable(drawable)
    }

    private fun setEnableButtons(enable: Boolean) {
        buttons.forEach {
            it.isEnabled = enable
        }
    }


}
