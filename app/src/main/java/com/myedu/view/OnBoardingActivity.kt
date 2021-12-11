package com.myedu.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.myedu.adapter.OnBoardingAdapter
import com.myedu.databinding.ActivityOnBoardingBinding

class OnBoardingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnBoardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnBoardingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.viewPager.adapter =  OnBoardingAdapter()
    }
}