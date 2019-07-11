package com.hnimrod.apngsample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.hnimrod.apngsample.databinding.ActivityMainBinding
import com.linecorp.apng.ApngDrawable

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
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

}
