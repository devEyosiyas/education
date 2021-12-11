package com.myedu.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.myedu.R


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_MyEdu);
        setContentView(R.layout.activity_main)
    }
}