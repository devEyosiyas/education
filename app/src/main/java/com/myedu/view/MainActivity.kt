package com.myedu.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.fragment.NavHostFragment
import com.myedu.R
import com.myedu.databinding.ActivityMainBinding
import com.myedu.utils.PrefManager


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setTheme(R.style.Theme_MyEdu);
        setContentView(R.layout.activity_main)
        val pref = PrefManager(this)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentHost) as NavHostFragment
        val navController = navHostFragment.navController

        val navGraph = navController.navInflater.inflate(R.navigation.main_navigation)
        navGraph.startDestination =
            if (pref.isFirsTimer)
                R.id.onBoardingFragment
            else
                R.id.loginFragment
        navController.graph = navGraph
    }
}