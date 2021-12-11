package com.myedu.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.myedu.R
import com.myedu.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_MyEdu);
        setContentView(R.layout.activity_main)

        startActivity(Intent(this,OnBoardingActivity::class.java))
    }
}