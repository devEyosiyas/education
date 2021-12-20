package com.myedu.view

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.myedu.R
import com.myedu.databinding.ActivityMainBinding
import com.myedu.utils.PrefManager


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setTheme(R.style.Theme_MyEdu);
        binding = ActivityMainBinding.inflate(layoutInflater)
        setSupportActionBar(binding.toolbar)
        setContentView(binding.root)
        auth = Firebase.auth
        val currentUser = auth.currentUser
        val pref = PrefManager(this)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentHost) as NavHostFragment
        val navController = navHostFragment.navController

        val navGraph = navController.navInflater.inflate(R.navigation.main_navigation)
        when {
            pref.isFirsTimer -> R.id.onBoardingFragment
            currentUser != null -> R.id.mainFragment
            else -> R.id.loginFragment
        }.also { navGraph.startDestination = it }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.onBoardingFragment || destination.id == R.id.loginFragment || destination.id == R.id.signUpFragment) {
                binding.appBar.visibility = View.GONE
                binding.bottomNavView.visibility = View.GONE
            } else {
                binding.appBar.visibility = View.VISIBLE
                binding.bottomNavView.visibility = View.VISIBLE
            }
            if (destination.id == R.id.mainFragment)
                binding.appBar.visibility = View.GONE
        }
        navController.graph = navGraph
        binding.bottomNavView.setupWithNavController(navController)
        binding.toolbar.setupWithNavController(navController)
    }
}